package com.xiaosa.filmagent.config;

import com.xiaosa.filmagent.entity.agentprompt.AssistantEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AssistantConfigurationTest {
    @Resource
    private AssistantEntity assistant;
    @Test
    void assistantConfig() {
        System.out.println(assistant.getRandomPrompt());
    }
}