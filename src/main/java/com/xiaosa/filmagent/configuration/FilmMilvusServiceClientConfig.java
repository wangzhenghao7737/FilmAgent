package com.xiaosa.filmagent.configuration;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilmMilvusServiceClientConfig {
    @Bean
    public MilvusServiceClient filmMilvusServiceClient(@Value("${spring.ai.vectorstore.milvus.client.host}") String host
                                                        ,@Value("${spring.ai.vectorstore.milvus.client.port}") int port) {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(host)
                .withPort( port)
                .build();
        return new MilvusServiceClient(connectParam);
    }
}
