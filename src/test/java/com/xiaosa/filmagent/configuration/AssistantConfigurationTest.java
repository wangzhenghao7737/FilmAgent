package com.xiaosa.filmagent.configuration;

import com.xiaosa.filmagent.entity.agentprompt.AssistantEntity;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AssistantConfigurationTest {
    @Resource
    private AssistantEntity assistant;
    @Test
    void assistantConfig() {
        System.out.println(assistant.getRandomPrompt());
    }
}