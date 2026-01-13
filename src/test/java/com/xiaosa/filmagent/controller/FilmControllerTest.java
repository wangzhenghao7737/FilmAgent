package com.xiaosa.filmagent.controller;

import com.xiaosa.filmagent.entity.agentresponse.ApiResponse;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    @Resource
    private FilmController filmController;
    @Test
    void simpleChat() {
        ApiResponse<String> result = filmController.simpleChat("你好", "1");
        System.out.println( result);
    }
}