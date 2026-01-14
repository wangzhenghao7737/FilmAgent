package com.xiaosa.filmagent.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class SimpleChatWithTools {
    @Resource
    private ChatClient FilmChatClient;
    @Resource
    private ToolCallback[] allTools;
    public String chat(String question,String conversationId) {
        ChatResponse chatResponse = FilmChatClient.prompt()
                .user(question)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }
}
