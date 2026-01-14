package com.xiaosa.filmagent.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SimpleChatWithToolsTest {

    @Resource
    private SimpleChatWithTools simpleChatWithTools;
    @Test
    void chat() {
        String chat = simpleChatWithTools.chat("该死，再给我介绍影片《绿巨人》，可以给我一些相关图片，或帮我保存相关的经典台词", "topic_green_test");
        System.out.println(chat);
    }
}