package com.xiaosa.filmagent.aitools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class WebScrapingTool {
    @Tool(description = "Scrapes a web page and returns its content")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        Document document = null;
        try {
            document = Jsoup.connect( url).get();
        } catch (Exception e) {
            return "Error scraping web page: " + e.getMessage();
        }
        return document.html();
    }
}
