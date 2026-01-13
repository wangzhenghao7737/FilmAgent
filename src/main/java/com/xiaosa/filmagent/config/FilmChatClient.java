package com.xiaosa.filmagent.config;

import com.xiaosa.filmagent.advisor.FilmLoggingAdvisor;
import com.xiaosa.filmagent.chatmemory.FileBasedChatMemory;
import com.xiaosa.filmagent.entity.agentprompt.AssistantEntity;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilmChatClient {
    @Resource
    private AssistantEntity assistantEntity;
    @Bean("FilmChatClient")
    public ChatClient filmChatClient(ChatModel dashscopeChatModel) {
        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(assistantEntity.getRandomPrompt())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(new FileBasedChatMemory())
                                .build()
                        ,
                        new FilmLoggingAdvisor()
                )
                .build();
    }
}
