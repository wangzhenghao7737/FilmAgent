package com.xiaosa.filmagent.config;

import com.xiaosa.filmagent.properties.AmapProperties;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class AiToolsConfig {
    @Resource
    private AmapProperties amapProperties;
    @Bean
    RestClient weatherRestClient() {
        return RestClient.builder()
                .baseUrl(amapProperties.getBase())
                .requestInterceptor((request, body, execution) -> {
                    // 打印完整 URL（包含 query params）
                    System.out.println(request.getURI());
                    return execution.execute(request, body);
                })
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
