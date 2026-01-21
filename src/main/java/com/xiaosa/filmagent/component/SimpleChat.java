package com.xiaosa.filmagent.component;

import com.xiaosa.filmagent.advisor.FilmLoggingAdvisor;
import com.xiaosa.filmagent.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SimpleChat {
    @Resource(name = "FilmChatClient")
    private ChatClient filmChatClient;

    public String chat(String question,String conversationId) {
        ChatResponse chatResponse = filmChatClient
                .prompt()
                .user(question)
                .advisors(spec->spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(
                        MessageChatMemoryAdvisor
                                .builder(new FileBasedChatMemory())
                                .build()
                        ,
                        new FilmLoggingAdvisor()
                )
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }
    public Flux<String> chatStream(String question,String conversationId) {
        return filmChatClient
                .prompt()
                .user(question)
                .advisors(spec->spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(
                        MessageChatMemoryAdvisor
                                .builder(new FileBasedChatMemory())
                                .build()
                        ,
                        new FilmLoggingAdvisor()
                )
                .stream()
                .content();
    }
}
