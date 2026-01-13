package com.xiaosa.filmagent.entity.agentprompt;

import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
public class AssistantEntity {
    private List<PromptEntity> assistant;
    private List<PromptEntity> character;
    private List<PromptEntity> tone;
    public String getRandomPrompt(){
        Random random = new Random();
        String assistantPrompt = assistant.get(random.nextInt(0, assistant.size())).getPrompt();
        String characterPrompt = character.get(random.nextInt(0, character.size())).getPrompt();
        String tonePrompt = tone.get(random.nextInt(0, tone.size())).getPrompt();
        return assistantPrompt +
                "你的性格：" +
                characterPrompt +
                "你的腔调：" +
                tonePrompt;
    }

    @Override
    public String toString() {
        return "AssistantEntity{" +
                "assistant=" + assistant +
                ", character=" + character +
                ", tone=" + tone +
                '}';
    }

    public List<PromptEntity> getAssistant() {
        return assistant;
    }

    public void setAssistant(List<PromptEntity> assistant) {
        this.assistant = assistant;
    }

    public List<PromptEntity> getCharacter() {
        return character;
    }

    public void setCharacter(List<PromptEntity> character) {
        this.character = character;
    }

    public List<PromptEntity> getTone() {
        return tone;
    }

    public void setTone(List<PromptEntity> tone) {
        this.tone = tone;
    }
}
