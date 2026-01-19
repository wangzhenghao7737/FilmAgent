package com.xiaosa.filmagent.configuration;

import com.xiaosa.filmagent.advisor.FilmLoggingAdvisor;
import com.xiaosa.filmagent.entity.agentprompt.AssistantEntity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilmChatWithTools {
    @Bean("FilmChatWithTools")
    public ChatClient filmChatWithTools(ChatModel dashscopeChatModel
            , AssistantEntity assistantEntity
            , ToolCallback[]  allTools) {
        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(assistantEntity.getRandomPrompt())
                .defaultAdvisors(
                        new FilmLoggingAdvisor()
                )
                .defaultToolCallbacks(allTools)
                .build();
    }
}
