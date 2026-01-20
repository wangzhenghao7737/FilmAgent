package com.xiaosa.filmagent.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.xiaosa.filmagent.agent.model.AgentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class ToolCallAgent extends ReActAgent{
    private static final Logger log = LoggerFactory.getLogger(ToolCallAgent.class);
    // 工具列表
    private final ToolCallback[] availableTools;
    // 临时变量(模型的回复结果)
    private ChatResponse toolCallChatResponse;
    // 工具调用者
    private final ToolCallingManager toolCallingManager;
    // 禁用SpringAI内部的工具调用机制
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
    }
    @Override
    public boolean think() {
        try{
            // 1校验提示词
            if(!StringUtils.hasText(getNextPrompt())){
                UserMessage userMessage = new UserMessage(getNextPrompt());
                getMessageList().add(userMessage);
            }
            // 2调用大模型
            Prompt prompt = new Prompt(getMessageList(), getChatOptions());
            // 3解析模型返回结果
            this.toolCallChatResponse = getChatClient()
                    .prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
            // 处理助手信息
            AssistantMessage assistantMessage = toolCallChatResponse.getResult().getOutput();
            final List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            String assistantMessageText = assistantMessage.getText();
            log.info("Assistant Thinking: {} \n Used {} tools \n Tool list :{}"
                    ,assistantMessageText
                    ,toolCalls.size()
                    , toolCalls.stream()
                            .map(toolCall -> String.format("%s: %s", toolCall.name(), toolCall.arguments()))
                            .collect(Collectors.joining("\n")));
            if(toolCalls.isEmpty()){
                // 不需要调用工具，手动维护消息
                getMessageList().add(assistantMessage);
                return false;
            }else {
                // 工具调用会维护消息
                return true;
            }
        }catch (Exception e){
            log.error("{} has error :{}", getName(), e.getMessage());
            getMessageList().add(new AssistantMessage("处理时遇到了错误："+e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        if(!toolCallChatResponse.hasToolCalls()){
            return "无需工具调用";
        }
        Prompt prompt = new Prompt(getMessageList(), getChatOptions());
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage)toolExecutionResult.conversationHistory().getLast();

        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> toolResponse.name().equals("doTerminate"));
        if(terminateToolCalled){
            setStatus(AgentStatus.FINISHED);
        }
        String result = toolResponseMessage.getResponses().stream()
                .map(response -> "工具：" + response.name() + ",结果：" + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info("Tool Call Agent ACT: {}", result);
        return result;
    }

    public ToolCallback[] getAvailableTools() {
        return availableTools;
    }

    public ChatResponse getToolCallChatResponse() {
        return toolCallChatResponse;
    }

    public void setToolCallChatResponse(ChatResponse toolCallChatResponse) {
        this.toolCallChatResponse = toolCallChatResponse;
    }

    public ToolCallingManager getToolCallingManager() {
        return toolCallingManager;
    }

    public ChatOptions getChatOptions() {
        return chatOptions;
    }
}
