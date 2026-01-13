package com.xiaosa.filmagent.chatmemory;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StoredMessage {
    public final String role;
    public final String content;

    @JsonCreator
    public StoredMessage(@JsonProperty("role") String role,
                         @JsonProperty("content") String content) {
        this.role = role;
        this.content = content;
    }
}