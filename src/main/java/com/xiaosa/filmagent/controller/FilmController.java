package com.xiaosa.filmagent.controller;

import com.xiaosa.filmagent.component.ChatWithRag;
import com.xiaosa.filmagent.component.SimpleChat;
import com.xiaosa.filmagent.component.SimpleChatWithTools;
import com.xiaosa.filmagent.entity.agentresponse.ApiResponse;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * spring mvc 如下返回类型实现流式输出
 *      Flux<String>
 *      SseEmitter
 *      Flux<ServerSentEvent<String>>
 */

@RestController
@RequestMapping("/chat")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final ExecutorService sseExecutor = Executors.newCachedThreadPool();
    @Resource
    private SimpleChat simpleChat;
    @Resource
    private ChatWithRag chatWithRag;

    /**
     * 一次性回答
     */
    @GetMapping("/simple")
    public ApiResponse<String> simpleChat(@RequestParam String question, @RequestParam String conversationId){
        String result = simpleChat.chat(question,conversationId);
        System.out.println( result);
        return ApiResponse.success(result);
    }

    /**
     * 简单流式回答
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestParam(required = false) String question,
            @RequestParam(required = false) String conversationId) {
        log.info("SseEmitter");

        question = Objects.requireNonNullElse(question, "").isBlank() ? "你好" : question.trim();
        conversationId = Objects.requireNonNullElse(conversationId, "").isBlank() ? "default" : conversationId.trim();

        SseEmitter emitter = new SseEmitter(180_000L);

        String finalQuestion = question;
        String finalConversationId = conversationId;
        sseExecutor.submit(() -> {
            try {
                simpleChat.chatStream(finalQuestion, finalConversationId)
                        .doOnNext(chunk -> {
                            if (chunk != null && !chunk.isEmpty()) {
                                try {
                                    emitter.send(chunk);
                                } catch (IOException e) {
                                    // 客户端已断开（如用户关页面），属于正常现象，无需报错
                                    log.debug("SSE client disconnected: {}", e.getMessage());
                                    // 可选：主动取消上游流
                                    // throw new RuntimeException("Client disconnected");
                                }
                            }
                        })
                        .doOnError(error -> {
                            log.error("AI streaming error", error);
                            try {
                                emitter.completeWithError(error);
                            } catch (Exception ex) {
                                log.warn("Failed to completeWithError (client gone?)", ex);
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                emitter.complete(); // 正常结束
                            } catch (Exception ex) {
                                log.warn("Failed to complete emitter (already closed?)", ex);
                            }
                        })
                        .blockLast(Duration.ofMinutes(2));
            } catch (Exception e) {
                log.error("Unexpected error in SSE task", e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.warn("Failed to report error to emitter", ex);
                }
            }
        });

        return emitter;
    }
    /**
     * 简单流式RAG回答
     */
    @GetMapping(value = "/rag", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> ragChat(@RequestParam String question, @RequestParam String conversationId){
        log.info("flux");
        return chatWithRag.chatRag(question,conversationId);
    }

    @GetMapping("/sse2")
    public Flux<ServerSentEvent<String>> chatByStreamServerSentEvent(String question, String conversationId) {
        return simpleChat.chatStream(question, conversationId)
                .map(chunk-> ServerSentEvent.<String>builder()
                        .data( chunk)
                        .build());
    }
}
