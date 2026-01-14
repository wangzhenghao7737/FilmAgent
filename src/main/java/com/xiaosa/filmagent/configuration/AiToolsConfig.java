package com.xiaosa.filmagent.configuration;

import com.xiaosa.filmagent.aitools.*;
import com.xiaosa.filmagent.properties.AmapProperties;
import jakarta.annotation.Resource;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class AiToolsConfig {
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
    @Bean
    ToolCallback[] allTools(EmailServiceTool emailServiceTool
            ,FileOperationTool fileOperationTool
            ,ResourcesDownloadTool resourcesDownloadTool
            ,WeatherTool weatherTool
            ,WebScrapingTool webScrapingTool
            ,WebSearchTool webSearchTool
                            ) {
        return ToolCallbacks.from(emailServiceTool
                , fileOperationTool
                , resourcesDownloadTool
                , weatherTool
                , webScrapingTool
                , webSearchTool);
    }
}
