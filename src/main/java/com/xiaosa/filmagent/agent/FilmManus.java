package com.xiaosa.filmagent.agent;

import com.xiaosa.filmagent.advisor.FilmLoggingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class FilmManus extends ToolCallAgent{
    public FilmManus(ToolCallback[] allTools , ChatModel dashscopeChatModel) {
        super(allTools);
        setName("Film-Manus");
        String SYSTEM_PROMPT = """
                You are Film-Manus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
        """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT =  """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        setNextPrompt(NEXT_STEP_PROMPT);
        setMaxSteps(10);
        ChatClient client = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new FilmLoggingAdvisor())
                .build();
        this.setChatClient(client);
    }

}
