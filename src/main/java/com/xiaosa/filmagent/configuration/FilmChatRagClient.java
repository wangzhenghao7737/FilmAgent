package com.xiaosa.filmagent.configuration;

import com.xiaosa.filmagent.advisor.FilmLoggingAdvisor;
import com.xiaosa.filmagent.advisor.SensitiveWordAdvisor;
import com.xiaosa.filmagent.chatmemory.FileBasedChatMemory;
import com.xiaosa.filmagent.entity.agentprompt.AssistantEntity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilmChatRagClient {
    @Bean("FilmChatRagClient")
    public ChatClient filmChatRagClient(ChatModel dashscopeChatModel, AssistantEntity assistantEntity) {
        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(assistantEntity.getRandomPrompt())
                .defaultAdvisors(
                       MessageChatMemoryAdvisor
                                .builder(new FileBasedChatMemory())
                                .build()
                        ,new FilmLoggingAdvisor()
                )
                .build();
    }
}
