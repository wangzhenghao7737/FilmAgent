package com.xiaosa.filmagent.service;

import com.xiaosa.filmagent.component.SimpleChatWithTools;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SimpleChatWithToolsTest {

    @Resource
    private SimpleChatWithTools simpleChatWithTools;
    @Test
    void chat() {
        String chat = simpleChatWithTools.chat("该死，给我介绍影片《赛车总动员》，可以给我一些相关图片", "topic_car_test");
        System.out.println(chat);
    }
}