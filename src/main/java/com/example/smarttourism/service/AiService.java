package com.example.smarttourism.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;

/**
 * AI 服务接口
 */
public interface AiService {
    /**
     * 流式对话
     * @param userMessage 用户输入的消息
     * @param emitter SSE 发射器，用于推流
     */
    void streamChat(String userMessage, SseEmitter emitter) throws IOException;
}
