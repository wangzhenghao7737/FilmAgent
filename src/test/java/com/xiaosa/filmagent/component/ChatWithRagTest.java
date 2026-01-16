package com.xiaosa.filmagent.component;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ChatWithRagTest {
    @Resource
    private ChatWithRag chatWithRag;

    @Test
    void chat() {
        String result = chatWithRag.chat("推荐个科幻电影（Science Fiction Movies）,有关盗梦者。", "rag_science_fiction");
        System.out.println(result);
    }
}