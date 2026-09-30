package com.supportorchestrator.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(@NotBlank @Size(max = 128) String sessionId,
                          @Size(max = 128) String customerId,
                          @NotBlank @Size(max = 4000) String message) {
}
