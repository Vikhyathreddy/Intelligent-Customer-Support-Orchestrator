package com.supportorchestrator.chat;

import com.supportorchestrator.agent.TicketClassification;
import com.supportorchestrator.ticket.EscalationReason;

import java.util.List;

public record ChatResponse(Long ticketId,
                           String answer,
                           boolean escalated,
                           EscalationReason escalationReason,
                           TicketClassification.Category category,
                           TicketClassification.Urgency urgency,
                           List<String> sources) {
}
