package com.supportorchestrator.chat;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SupportOrchestrator orchestrator;

    public ChatController(SupportOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return orchestrator.handle(request);
    }
}
