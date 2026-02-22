package com.example.smarttourism.controller;

import com.example.smarttourism.service.AiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 智谱 AI 对话接口
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // 👈 🔥 关键修改：加上这一行，允许前端跨域访问！
public class AiController {

    private static final long SSE_TIMEOUT_MS = 120_000L;

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * 流式对话（推荐 GET，便于前端 EventSource）
     */
    @GetMapping(value = "/chat", produces = "text/event-stream")
    public SseEmitter chatGet(@RequestParam("message") String message) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitter.onTimeout(emitter::complete);
        emitter.onError(Throwable::printStackTrace);
        runStreamChatAsync(message, emitter);
        return emitter;
    }

    /**
     * 流式对话（POST，前端用的是这个）
     */
    @PostMapping(value = "/chat", produces = "text/event-stream")
    public SseEmitter chatPost(@RequestBody ChatRequest request) {
        String message = request != null && request.getMessage() != null ? request.getMessage() : "";
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitter.onTimeout(emitter::complete);
        emitter.onError(Throwable::printStackTrace);
        runStreamChatAsync(message, emitter);
        return emitter;
    }

    /** 异步执行流式对话 */
    private void runStreamChatAsync(String message, SseEmitter emitter) {
        // 开启一个新线程去处理 AI 请求，防止卡住主线程
        new Thread(() -> {
            try {
                aiService.streamChat(message, emitter);
            } catch (Exception e) {
                try {
                    emitter.completeWithError(e);
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    // 如果你的项目里有 Lombok，这样写没问题
    // 如果报错找不到 @lombok.Data，可以把 @lombok.Data 删掉，手动生成 Getter/Setter
    @lombok.Data
    public static class ChatRequest {
        private String message;
    }
}