package com.supportorchestrator.chat;

import com.supportorchestrator.TestProperties;
import com.supportorchestrator.agent.TicketClassification;
import com.supportorchestrator.agent.TicketClassification.Category;
import com.supportorchestrator.agent.TicketClassification.Urgency;
import com.supportorchestrator.knowledge.RetrievedChunk;
import com.supportorchestrator.ticket.EscalationReason;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EscalationPolicyTest {

    private final EscalationPolicy policy = new EscalationPolicy(TestProperties.defaults());

    @Test
    void escalatesCriticalIssuesBeforeRetrieval() {
        var classification = new TicketClassification(Category.TECHNICAL, Urgency.CRITICAL, false, "outage");
        assertThat(policy.beforeRetrieval(classification)).contains(EscalationReason.CRITICAL_URGENCY);
    }

    @Test
    void escalatesWhenCustomerAsksForHuman() {
        var classification = new TicketClassification(Category.BILLING, Urgency.LOW, true, "wants a person");
        assertThat(policy.beforeRetrieval(classification)).contains(EscalationReason.CUSTOMER_REQUESTED_HUMAN);
    }

    @Test
    void letsRoutineQuestionsThrough() {
        var classification = new TicketClassification(Category.HOW_TO, Urgency.LOW, false, "how to");
        assertThat(policy.beforeRetrieval(classification)).isEmpty();
    }

    @Test
    void escalatesWhenNoChunkIsRelevantEnough() {
        assertThat(policy.beforeAnswer(List.of())).contains(EscalationReason.LOW_RETRIEVAL_CONFIDENCE);
        assertThat(policy.beforeAnswer(List.of(new RetrievedChunk("t", "a.md", 0.60))))
                .contains(EscalationReason.LOW_RETRIEVAL_CONFIDENCE);
        assertThat(policy.beforeAnswer(List.of(new RetrievedChunk("t", "a.md", 0.80)))).isEmpty();
    }

    @Test
    void escalatesWhenModelSaysAnswerIsNotInKnowledgeBase() {
        assertThat(policy.afterAnswer("[[NO_ANSWER]]")).contains(EscalationReason.NOT_IN_KNOWLEDGE_BASE);
        assertThat(policy.afterAnswer("  ")).contains(EscalationReason.NOT_IN_KNOWLEDGE_BASE);
        assertThat(policy.afterAnswer("Run the installer as administrator. [installation-guide.md]"))
                .isEqualTo(Optional.empty());
    }
}
