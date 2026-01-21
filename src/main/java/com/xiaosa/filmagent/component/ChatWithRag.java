package com.xiaosa.filmagent.component;

import com.xiaosa.filmagent.advisor.FilmLoggingAdvisor;
import com.xiaosa.filmagent.advisor.SensitiveWordAdvisor;
import com.xiaosa.filmagent.chatmemory.FileBasedChatMemory;
import com.xiaosa.filmagent.rag.CustomVectorStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ChatWithRag {
    private final ChatClient filmChatRagClient;
    private final CustomVectorStore vectorStore;
    public ChatWithRag(ChatClient FilmChatRagClient
                        , CustomVectorStore vectorStore) {
        this.filmChatRagClient = FilmChatRagClient;
        this.vectorStore = vectorStore;
    }
    public String chat(String question,String conversationId) {
        ChatResponse chatResponse = filmChatRagClient
                .prompt()
                .user(question)
                .advisors(spec->spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(
                        MessageChatMemoryAdvisor
                                .builder(new FileBasedChatMemory())
                                .build()
                        ,vectorStore.getAdvisor())
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }
    public Flux<String> chatRag(String question, String conversationId) {
        return filmChatRagClient
                .prompt()
                .user(question)
                .advisors(spec->spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(
                        MessageChatMemoryAdvisor
                                .builder(new FileBasedChatMemory())
                                .build()
                        ,vectorStore.getAdvisor())
                .stream()
                .content();
            }
}
