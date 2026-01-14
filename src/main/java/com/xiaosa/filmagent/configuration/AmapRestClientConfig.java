package com.xiaosa.filmagent.configuration;

import com.xiaosa.filmagent.properties.AmapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
/**
 * 高德地图 RestClient 配置
 */
@Configuration
public class AmapRestClientConfig {
    @Bean
    RestClient weatherRestClient(AmapProperties amapProperties) {
        return RestClient.builder()
                .baseUrl(amapProperties.getBase())
//                .requestInterceptor((request, body, execution) -> {
//                    // 打印完整 URL（包含 query params）
//                    System.out.println(request.getURI());
//                    return execution.execute(request, body);
//                })
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
