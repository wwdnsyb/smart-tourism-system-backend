package com.example.smarttourism.service.impl;

import com.example.smarttourism.entity.ScenicSpot;
import com.example.smarttourism.repository.ScenicSpotRepository;
import com.example.smarttourism.service.AiService;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Value("${zhipu.api-key}")
    private String apiKey;

    private ClientV4 client;

    private final ScenicSpotRepository scenicSpotRepository;

    public AiServiceImpl(ScenicSpotRepository scenicSpotRepository) {
        this.scenicSpotRepository = scenicSpotRepository;
    }

    @PostConstruct
    public void init() {
        this.client = new ClientV4.Builder(apiKey).build();
    }

    @Override
    public void streamChat(String userMessage, SseEmitter emitter) throws IOException {
        // 1. RAG 检索：查库
        List<ScenicSpot> allSpots = scenicSpotRepository.findAll();
        StringBuilder dbKnowledge = new StringBuilder();
        boolean hitDb = false;

        for (ScenicSpot spot : allSpots) {
            if (userMessage.contains(spot.getName())) {
                hitDb = true;
                log.info("🎯 命中数据库知识库：{}", spot.getName());
                // 拼接 JSON 数据
                dbKnowledge.append(String.format(
                        "{\"景点名称\":\"%s\", \"门票价格\":\"%s\", \"开放时间\":\"%s\", \"地理位置\":\"%s\", \"特色简介\":\"%s\"}\n",
                        spot.getName(),
                        spot.getPrice(),
                        spot.getOpenTime(),
                        spot.getAddress(), // 👈 这里改成了 getAddress()
                        spot.getDescription()
                ));
            }
        }

        // 2. 构建【绝对专注版】System Prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是一个【智慧旅游管理平台】的专属AI智能导游，名字叫“云游小助手”。\n");
        promptBuilder.append("你的唯一职责是回答关于【旅游、景点、美食、住宿、交通、行程规划】的问题。\n\n");

        promptBuilder.append("### 🚫 严厉的回复限制（必须遵守）：\n");
        promptBuilder.append("1. **绝对禁止跑题**：如果用户问的问题与旅游无关（例如：雅思、编程、数学、情感、政治、历史作业等），请**直接拒绝**。\n");
        promptBuilder.append("2. **禁止提供建议**：在拒绝时，**严禁**提供任何相关的建议、资源或步骤。不要说“不过我可以给你一些建议...”，要彻底切断非旅游话题。\n");
        promptBuilder.append("3. **话术范例**：\n");
        promptBuilder.append("   - 错误回答：“虽然我不教雅思，但你可以去买官方指南...” (❌ 绝对禁止)\n");
        promptBuilder.append("   - 正确回答：“抱歉，我只是一个旅游助手，不懂雅思哦。不过如果您想去英国旅游，我倒是可以为您介绍一下伦敦的景点！🏰” (✅ 正确)\n\n");

        promptBuilder.append("### ✅ 正常旅游问答规则：\n");
        promptBuilder.append("1. **依据数据**：如果下文提供了【数据库真实数据】，请严格基于数据回答（价格、时间必须精准）。\n");
        promptBuilder.append("2. **排版要求**：使用 Markdown 格式（加粗关键信息、使用列表）。\n");
        promptBuilder.append("3. **语气风格**：热情、专业，多使用 Emoji 图标（如 🏔️, 🎫, 🍜）。\n");

        if (hitDb) {
            promptBuilder.append("\n### 🔍 检索到的【数据库真实数据】(以此为准)：\n");
            promptBuilder.append(dbKnowledge.toString());
        }

        String systemPrompt = promptBuilder.toString();
        log.info("🤖 System Prompt 长度: {}", systemPrompt.length());

        // 3. 构造请求
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("glm-4-flash")
                .stream(Boolean.TRUE)
                .messages(messages)
                .build();

        // 4. 发起调用
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);

        if (sseModelApiResp.isSuccess()) {
            Flowable<ModelData> flowable = sseModelApiResp.getFlowable();
            flowable.map(modelData -> {
                if (modelData.getChoices() == null || modelData.getChoices().isEmpty()) return "";
                String content = modelData.getChoices().get(0).getDelta().getContent();
                return content == null ? "" : content;
            }).subscribe(
                    content -> {
                        if (content != null && !content.isEmpty()) {
                            // 保持 [BR] 替换逻辑，配合前端 buffer 机制
                            String safeContent = content.replace("\n", "[BR]");
                            try {
                                emitter.send(safeContent);
                            } catch (IOException e) {
                                log.error("前端连接断开", e);
                                emitter.completeWithError(e);
                            }
                        }
                    },
                    error -> {
                        log.error("AI 响应异常", error);
                        try { emitter.completeWithError(error); } catch (Exception ignored) {}
                    },
                    () -> {
                        log.info("✅ AI 回答完毕");
                        try { emitter.complete(); } catch (Exception ignored) {}
                    }
            );
        } else {
            String errorMsg = "AI 服务繁忙: " + sseModelApiResp.getMsg();
            try {
                emitter.send(errorMsg);
                emitter.complete();
            } catch (IOException e) {
                log.error("发送错误信息失败", e);
            }
        }
    }
}