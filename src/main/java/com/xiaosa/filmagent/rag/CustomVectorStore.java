package com.xiaosa.filmagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class CustomVectorStore {
    private final VectorStore vectorStore;
    public CustomVectorStore(VectorStore MilvusVectorStore) {
        this.vectorStore = MilvusVectorStore;
    }
    public Advisor getAdvisor() {
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(3)
                .similarityThreshold(0.2)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .order(100)
                .build();
    }
}
