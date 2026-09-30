package com.supportorchestrator.chat;

import com.supportorchestrator.agent.SupportAgent;
import com.supportorchestrator.agent.TicketClassification;
import com.supportorchestrator.agent.TicketClassification.Urgency;
import com.supportorchestrator.config.AppProperties;
import com.supportorchestrator.knowledge.RetrievedChunk;
import com.supportorchestrator.ticket.EscalationReason;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Decides when the agent must hand a conversation to a human. Kept free of I/O so the rules are easy
 * to test and tune.
 */
@Component
public class EscalationPolicy {

    private final double answerableScore;

    public EscalationPolicy(AppProperties props) {
        this.answerableScore = props.retrieval().answerableScore();
    }

    /** Checked before retrieval: some tickets should never be handled by the bot. */
    public Optional<EscalationReason> beforeRetrieval(TicketClassification classification) {
        if (classification.requestsHuman()) {
            return Optional.of(EscalationReason.CUSTOMER_REQUESTED_HUMAN);
        }
        if (classification.urgency() == Urgency.CRITICAL) {
            return Optional.of(EscalationReason.CRITICAL_URGENCY);
        }
        return Optional.empty();
    }

    /** Checked before calling the LLM: without relevant documentation there is nothing to ground an answer in. */
    public Optional<EscalationReason> beforeAnswer(List<RetrievedChunk> chunks) {
        double best = chunks.stream().mapToDouble(RetrievedChunk::score).max().orElse(0);
        return best < answerableScore ? Optional.of(EscalationReason.LOW_RETRIEVAL_CONFIDENCE) : Optional.empty();
    }

    /** Checked on the model's reply. */
    public Optional<EscalationReason> afterAnswer(String answer) {
        if (answer == null || answer.isBlank() || answer.contains(SupportAgent.NO_ANSWER)) {
            return Optional.of(EscalationReason.NOT_IN_KNOWLEDGE_BASE);
        }
        return Optional.empty();
    }
}
