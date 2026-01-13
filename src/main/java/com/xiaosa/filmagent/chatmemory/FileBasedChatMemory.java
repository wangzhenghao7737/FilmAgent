package com.xiaosa.filmagent.chatmemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xiaosa.filmagent.constant.FilmConstant;
import com.xiaosa.filmagent.entity.agentresponse.FilmEnum;
import com.xiaosa.filmagent.exception.FilmException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBasedChatMemory implements ChatMemory {

    private static final Logger logger = LoggerFactory.getLogger(FileBasedChatMemory.class);
    private final String BASE_DIR;
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public FileBasedChatMemory() {
        this.BASE_DIR = FilmConstant.CHAT_MEMORY_PATH;
        File dir = new File(BASE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    /**
     * 实现了对话的存储和读取
     * 链式，会有两次调用，一次对话，先保存用户输入，后保存模型输出
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 从文件中读取
        List<StoredMessage> stored = get(conversationId).stream()
                .map(msg -> new StoredMessage(msg.getMessageType().getValue(), msg.getText()))
                .collect(Collectors.toList());
        // 长度限制
        if(stored.size()>=FilmConstant.FILM_DEFAULT_CHAT_MEMORY_SIZE){
            stored = stored.subList(stored.size()-FilmConstant.FILM_DEFAULT_CHAT_MEMORY_SIZE,stored.size());
        }
        // 添加新消息
        for (Message msg : messages) {
            stored.add(new StoredMessage(msg.getMessageType().getValue(), msg.getText()));
        }
        // 重新写入
        File file = getConversationFile(conversationId);
        try {
            mapper.writeValue(file, stored);
        } catch (IOException e) {
            throw new FilmException(FilmEnum.FAIL_TO_WRITE_CHAT_MEMORY_FROM_FILE);
        }
    }
    @Override
    public List<Message> get(String conversationId) {
        File file = getConversationFile(conversationId);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            List<StoredMessage> stored = mapper.readValue(file, new TypeReference<List<StoredMessage>>() {});
            return stored.stream().map(storedMsg -> {
                switch (storedMsg.role) {
                    case "user":
                        return new UserMessage(storedMsg.content);
                    case "assistant":
                        return new AssistantMessage(storedMsg.content);
                    case "system":
                        return new SystemMessage(storedMsg.content);
                    default:
                        logger.warn("Unknown message role: {}", storedMsg.role);
                        return new UserMessage(storedMsg.content); // fallback
                }
            }).collect(Collectors.toList());
        } catch (IOException e) {
            logger.warn("Failed to read chat memory, returning empty", e);
            return new ArrayList<>();
        }
    }
    @Override
    public void clear(String conversationId) {
        if (StringUtils.hasText(conversationId)) {
            File file = getConversationFile(conversationId);
            if (file.exists()) {
                if (!file.delete()) {
                    logger.warn("Failed to delete chat memory file: {}", file.getAbsolutePath());
                }
            }
        }
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".json");
    }
}
