package com.supportorchestrator.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Triage step: turns a free-text customer message into structured routing data.
 */
public interface TicketClassifier {

    @SystemMessage(fromResource = "prompts/classifier-system.txt")
    @UserMessage("Customer message:\n\"\"\"\n{{message}}\n\"\"\"")
    TicketClassification classify(@V("message") String message);
}
