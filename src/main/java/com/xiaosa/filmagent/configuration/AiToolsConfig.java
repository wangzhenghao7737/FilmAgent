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
    ToolCallback[] allTools(EmailServiceTool emailServiceTool
            ,FileOperationTool fileOperationTool
            ,ResourcesDownloadTool resourcesDownloadTool
            ,WeatherTool weatherTool
            ,WebScrapingTool webScrapingTool
            ,WebSearchTool webSearchTool
            ,ImageSearchTool imageSearchTool) {
        return ToolCallbacks.from(emailServiceTool
                , fileOperationTool
                , resourcesDownloadTool
                , weatherTool
                , webScrapingTool
                , webSearchTool
                , imageSearchTool);
    }
}
