package com.supportorchestrator.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Persists conversation history in Postgres, so sessions survive restarts and work across multiple
 * application instances behind a load balancer.
 */
@Component
public class PostgresChatMemoryStore implements ChatMemoryStore {

    private final ChatMemoryRepository repository;

    public PostgresChatMemoryStore(ChatMemoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ChatMessage> getMessages(Object sessionId) {
        return repository.findById(sessionId.toString())
                .map(entity -> ChatMessageDeserializer.messagesFromJson(entity.getMessagesJson()))
                .orElseGet(List::of);
    }

    @Override
    public void updateMessages(Object sessionId, List<ChatMessage> messages) {
        repository.save(new ChatMemoryEntity(sessionId.toString(), ChatMessageSerializer.messagesToJson(messages)));
    }

    @Override
    public void deleteMessages(Object sessionId) {
        repository.deleteById(sessionId.toString());
    }
}
