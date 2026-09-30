package com.supportorchestrator.ticket;

public enum TicketStatus {
    /** Answered by the agent without human involvement. */
    AUTO_RESOLVED,
    /** Handed to the human queue. */
    ESCALATED,
    /** Closed by a human after escalation. */
    RESOLVED_BY_HUMAN
}
