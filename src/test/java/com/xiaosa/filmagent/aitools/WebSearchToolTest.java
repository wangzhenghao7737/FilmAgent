package com.xiaosa.filmagent.aitools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSearchToolTest {
    @Resource
    private WebSearchTool webSearchTool;

    @Test
    void searchBaidu() {
        String s = webSearchTool.searchBaidu("影城");
        System.out.println(s);
    }
}