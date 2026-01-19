package com.xiaosa.filmagent.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaosa.filmagent.entity.agentprompt.AssistantEntity;
import com.xiaosa.filmagent.entity.agentprompt.PromptEntity;
import com.xiaosa.filmagent.entity.agentresponse.FilmEnum;
import com.xiaosa.filmagent.exception.FilmException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 读取自定义的prompt数据
 * 随机system prompt
 */

@Configuration
public class AssistantConfig {
    @Bean("AssistantEntity")
    public AssistantEntity assistantConfig(ObjectMapper objectMapper) {
        AssistantEntity assistantConfig = new AssistantEntity();
        // 1. 获取 resources 下的 JSON 文件（假设文件名为 assistants.json）
        ClassPathResource assistants_resource = new ClassPathResource("prompt/assistant.json");
        ClassPathResource character_resource = new ClassPathResource("prompt/character.json");
        ClassPathResource tone_resource = new ClassPathResource("prompt/tone.json");

        // 2. 打开输入流（自动处理 JAR 内资源）
        try (InputStream assistants_input = assistants_resource.getInputStream() ;
             InputStream character_input = character_resource.getInputStream();
             InputStream tone_input = tone_resource.getInputStream()) {
            // 3. 直接反序列化为 List<AssistantEntity>
            assistantConfig.setAssistant(objectMapper.readValue(assistants_input, new TypeReference<List<PromptEntity>>() {}));
            assistantConfig.setCharacter(objectMapper.readValue(character_input, new TypeReference<List<PromptEntity>>() {}));
            assistantConfig.setTone(objectMapper.readValue(tone_input, new TypeReference<List<PromptEntity>>() {}));
        } catch (IOException e) {
            throw new FilmException(FilmEnum.FAIL_TO_LOAD_PROMPT_FROM_FILE);
        }
        return assistantConfig;
    }
}
