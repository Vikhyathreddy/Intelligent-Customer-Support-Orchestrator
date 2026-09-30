package com.supportorchestrator.agent;

import dev.langchain4j.model.output.structured.Description;

public record TicketClassification(
        @Description("The single best-fitting category") Category category,
        @Description("How urgent the issue is for the customer") Urgency urgency,
        @Description("True if the customer explicitly asks to talk to a human") boolean requestsHuman,
        @Description("One-sentence summary of the issue, for the support agent's queue") String summary) {

    public enum Category {
        TECHNICAL, BILLING, ACCOUNT, HOW_TO, BUG_REPORT, FEATURE_REQUEST, OTHER
    }

    public enum Urgency {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
