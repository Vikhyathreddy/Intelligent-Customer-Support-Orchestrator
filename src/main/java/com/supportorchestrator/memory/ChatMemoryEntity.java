package com.supportorchestrator.memory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "chat_memory")
public class ChatMemoryEntity {

    @Id
    private String sessionId;

    @Column(columnDefinition = "text")
    private String messagesJson;

    private Instant updatedAt;

    protected ChatMemoryEntity() {
    }

    public ChatMemoryEntity(String sessionId, String messagesJson) {
        this.sessionId = sessionId;
        this.messagesJson = messagesJson;
        this.updatedAt = Instant.now();
    }

    public String getMessagesJson() {
        return messagesJson;
    }
}
