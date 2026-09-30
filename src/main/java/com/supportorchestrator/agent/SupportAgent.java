package com.supportorchestrator.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * The customer-facing agent. Answers strictly from the retrieved knowledge-base context and keeps
 * per-session conversation memory so follow-up questions work.
 */
public interface SupportAgent {

    /** Emitted by the model when the context does not contain the answer. */
    String NO_ANSWER = "[[NO_ANSWER]]";

    @SystemMessage(fromResource = "prompts/support-agent-system.txt")
    @UserMessage(fromResource = "prompts/support-agent-user.txt")
    String answer(@MemoryId String sessionId, @V("question") String question, @V("context") String context);
}
