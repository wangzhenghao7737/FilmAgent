package com.xiaosa.filmagent.component;

import com.xiaosa.filmagent.service.SimpleChat;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SimpleChatTest {
    @Resource
    private SimpleChat simpleChat;

    @Test
    void chat() {
        String res = simpleChat.chat("介绍一部电影", "112");
        System.out.println( res);
    }
}