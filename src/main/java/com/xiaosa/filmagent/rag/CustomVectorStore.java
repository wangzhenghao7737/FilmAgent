package com.xiaosa.filmagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
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
        final PromptTemplate emptyContext = PromptTemplate.builder()
                .template("已检索知识库，未发现相关信息")
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(ContextualQueryAugmenter
                        .builder()
                        .allowEmptyContext(true)
                        .emptyContextPromptTemplate(emptyContext)
                        .build())
                .build();
    }
}
