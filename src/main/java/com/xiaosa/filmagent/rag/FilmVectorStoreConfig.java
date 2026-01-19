package com.xiaosa.filmagent.rag;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 的设计原则：通过 VectorStore 接口编程，不依赖具体实现。这样未来切换 Redis、PGVector 等只需改配置，代码不变。
 * spring-ai-starter-vector-store-milvus 会自动配置并注入 VectorStore Bean
 */
//@Configuration
public class FilmVectorStoreConfig {
      //Spring AI 会自动创建 VectorStore Bean，类型为 MilvusVectorStore
//    @Bean
    public VectorStore FilmVectorStore(MilvusServiceClient milvusClient, EmbeddingModel dashscpoeEmbeddingModel) {
        return MilvusVectorStore.builder(milvusClient, dashscpoeEmbeddingModel)
                .collectionName("vector_store_film")
                .databaseName("default")
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
                .batchingStrategy(new TokenCountBatchingStrategy())
                .initializeSchema(true)
                .build();
    }
//    @Bean
//    public MilvusServiceClient milvusClient() {
//        return new MilvusServiceClient(ConnectParam.newBuilder()
//                .withAuthorization("admin", "admin")
//                .withUri(milvusContainer.getEndpoint())
//                .build());
//    }
}
