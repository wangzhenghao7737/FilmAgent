package com.xiaosa.filmagent.configuration;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Milvus 服务客户端配置
 * milvus数据库的metadata格式为：JSON。删除数据时需要使用客户端
 */
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
