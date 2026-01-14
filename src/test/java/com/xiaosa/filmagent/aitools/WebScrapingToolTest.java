package com.xiaosa.filmagent.aitools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String s = webScrapingTool.scrapeWebPage("https://www.baidu.com");
        System.out.println(s);
    }
}