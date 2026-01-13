package com.xiaosa.filmagent.advisor;

import com.xiaosa.filmagent.utils.DfaSensitiveWordFilter;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 敏感词过滤原理：DFA
 * 该类用于对AI回复内容尽心过滤，也可以拓展实现过滤用户的提问内容
 */

@Component
public class SensitiveWordAdvisor implements CallAdvisor, StreamAdvisor {

    private final DfaSensitiveWordFilter filter;

    // 假设敏感词从配置或数据库加载
    public SensitiveWordAdvisor() {
        this.filter = new DfaSensitiveWordFilter();
        // 初始化敏感词库（实际项目应从配置中心/DB加载）
        this.filter.addSensitiveWords(List.of("影片","电影"));
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        ChatClientResponse response = chain.nextCall(request);
        return sanitizeResponse(response);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        return chain.nextStream(request)
                .map(this::sanitizeResponse);
    }

    private ChatClientResponse sanitizeResponse(ChatClientResponse response) {
        String originalContent = response.chatResponse().getResult().getOutput().getText();
        String sanitizedContent = filter.filter(originalContent);
        // 构造新响应（保持其他字段不变）
        return ChatClientResponse.builder()
                .chatResponse(
                        ChatResponse.builder()
                                .generations(List.of(new Generation(new AssistantMessage(sanitizedContent))))
                                .build()
                )
                .context(Map.copyOf(response.context()))
                .build();
    }

    @Override
    public int getOrder() {
        return 100; // 在 SafeGuardAdvisor 之后执行（输出过滤）
    }


    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
