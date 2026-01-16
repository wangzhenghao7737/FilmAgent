package com.xiaosa.filmagent.rag;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 对切割后的文档进行关键词提取
 */
@Component
public class FilmKeywordEnricher {
    private final ChatModel dashscopeChatModel;

    FilmKeywordEnricher(ChatModel dashscopeChatModel ) {
        this.dashscopeChatModel = dashscopeChatModel;
    }

    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(dashscopeChatModel,3);
        return enricher.apply(documents);
    }
}