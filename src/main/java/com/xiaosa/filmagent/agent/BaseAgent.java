package com.xiaosa.filmagent.agent;

import com.xiaosa.filmagent.agent.model.AgentStatus;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
public abstract class BaseAgent {
    private static final Logger log = LoggerFactory.getLogger(BaseAgent.class);

    private String name;
    private String systemPrompt;
    private String nextPrompt;
    private AgentStatus status = AgentStatus.IDLE;

    private int currentStep = 0;
    private int maxSteps = 10;
    private ChatClient chatClient;
    private List<Message> messageList = new ArrayList<>();

    public String run(String userPrompt){
        // 基础校验
        if(this.status != AgentStatus.IDLE){
            throw new RuntimeException("Cannot run agent");
        }
        if(!StringUtils.hasText(userPrompt)){
            throw new RuntimeException("Cannot run agent : User prompt is empty");
        }
        // 初始化信息
        this.status = AgentStatus.RUNNING;
        this.messageList.add(new UserMessage(userPrompt));
        // 结果列表
        List<String> resultList = new ArrayList<>();
        try{
            for(int i=0;i<maxSteps;i++){
                currentStep = i;
                log.info("Executing Step: {}/{}",i,maxSteps);
                String stepResult = step();
                resultList.add("step "+i+": "+stepResult);
            }
            if(currentStep>=maxSteps){
                this.status = AgentStatus.FINISHED;
                resultList.add("Terminated : Reached max steps");
            }
            return String.join("\n",resultList);
        }catch (Exception e){
            this.status = AgentStatus.ERROR;
            log.error("Error running agent", e);
            return "Error running agent"+e.getMessage();
        }finally {
            cleanUp();
        }
    }

    // 流式执行
    public SseEmitter runStream(String userPrompt){
        SseEmitter sseEmitter = new SseEmitter(9 * 60 * 1000L);
        CompletableFuture.runAsync(() -> {
            // 基础校验
            try {
                if(this.status != AgentStatus.IDLE){
                    sseEmitter.send("Error running agent: Agent is not idle");
                    sseEmitter.complete();
                    return;
                }
                if(!StringUtils.hasText(userPrompt)){
                    sseEmitter.send("Cannot run agent : User prompt is empty");
                    sseEmitter.complete();
                    return;
                }
            }catch (IOException e){
                sseEmitter.completeWithError(e);
            }
            // 初始化信息
            this.status = AgentStatus.RUNNING;
            this.messageList.add(new UserMessage(userPrompt));
            // 结果列表
            List<String> resultList = new ArrayList<>();
            try {
                for(int i = 0; i < maxSteps; i++){
                    currentStep = i;
                    log.info("Executing: {}/{}", i,maxSteps);
                    //执行单步
                    String stepResult = step();
                    resultList.add("step "+i+": "+stepResult);
                    sseEmitter.send("step "+i+": "+stepResult);
                }
                if(currentStep>=maxSteps){
                    status = AgentStatus.FINISHED;
                    resultList.add("Terminated : Reached max step "+maxSteps);
                    sseEmitter.send("Terminated : Reached max step "+maxSteps);
                }
                sseEmitter.complete();
//                return String.join("\n",resultList);
            }catch (Exception e){
                status = AgentStatus.ERROR;
                log.error("Error running agent", e);
                try {
                    sseEmitter.send("Error running agent: "+e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
//               sseEmitter.complete();
            }finally {
                cleanUp();
            }
        });
        sseEmitter.onTimeout(() -> {
            this.status = AgentStatus.ERROR;
            this.cleanUp();
            log.warn("Agent timed out");
        });
        sseEmitter.onCompletion(() -> {
            if(this.status==AgentStatus.RUNNING){
                this.status = AgentStatus.FINISHED;
            }
            this.cleanUp();
            log.info("Agent completed");
        });
        return sseEmitter;
        }

    public abstract String step();
    protected void cleanUp(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getNextPrompt() {
        return nextPrompt;
    }

    public void setNextPrompt(String nextPrompt) {
        this.nextPrompt = nextPrompt;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
