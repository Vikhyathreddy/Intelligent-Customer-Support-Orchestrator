package com.supportorchestrator.memory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMemoryRepository extends JpaRepository<ChatMemoryEntity, String> {
}
