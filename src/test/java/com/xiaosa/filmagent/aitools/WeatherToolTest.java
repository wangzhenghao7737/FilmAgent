package com.xiaosa.filmagent.aitools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class WeatherToolTest {
    @Resource
    private WeatherTool weatherTool;
    @Test
    void getWeather() {
        String s = weatherTool.getWeather("上海");
        System.out.println(s);
    }
}