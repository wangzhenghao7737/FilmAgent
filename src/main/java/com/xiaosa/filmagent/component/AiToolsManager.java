package com.xiaosa.filmagent.component;

import com.xiaosa.filmagent.aitools.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class AiToolsManager {
    @Resource
    private EmailServiceTool emailServiceTool;
    @Resource
    private FileOperationTool fileOperationTool;
    @Resource
    private ResourcesDownloadTool resourcesDownloadTool;
    @Resource
    private WeatherTool weatherTool;
    @Resource
    private WebScrapingTool webScrapingTool;
    @Resource
    private WebSearchTool webSearchTool;

}
