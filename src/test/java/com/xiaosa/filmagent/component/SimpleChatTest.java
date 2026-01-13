package com.xiaosa.filmagent.component;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SimpleChatTest {
    @Resource
    private SimpleChat simpleChat;

    @Test
    void chat() {
        String res = simpleChat.chat("介绍一部电影", "11");
        System.out.println( res);
    }
}