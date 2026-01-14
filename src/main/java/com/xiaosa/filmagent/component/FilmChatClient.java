package com.xiaosa.filmagent.component;

import com.xiaosa.filmagent.advisor.FilmLoggingAdvisor;
import com.xiaosa.filmagent.advisor.SensitiveWordAdvisor;
import com.xiaosa.filmagent.chatmemory.FileBasedChatMemory;
import com.xiaosa.filmagent.entity.agentprompt.AssistantEntity;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FilmChatClient {
    @Resource
    private AssistantEntity assistantEntity;
    @Bean("FilmChatClient")
    public ChatClient filmChatClient(ChatModel dashscopeChatModel) {
        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(assistantEntity.getRandomPrompt())
                .defaultAdvisors(
                        SafeGuardAdvisor.builder()
                                .sensitiveWords(List.of("色情","暴力","政治"))
                                .failureResponse("I'm sorry, but I'm unable to process your request at this time. Please try again later.")
                                .order(0)
                                .build()
                        ,MessageChatMemoryAdvisor
                                .builder(new FileBasedChatMemory())
                                .build()
                        ,new SensitiveWordAdvisor()
                        ,new FilmLoggingAdvisor()
                )
                .build();
    }
}
