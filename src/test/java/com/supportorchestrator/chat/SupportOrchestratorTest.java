package com.supportorchestrator.chat;

import com.supportorchestrator.TestProperties;
import com.supportorchestrator.agent.SupportAgent;
import com.supportorchestrator.agent.TicketClassification;
import com.supportorchestrator.agent.TicketClassification.Category;
import com.supportorchestrator.agent.TicketClassification.Urgency;
import com.supportorchestrator.agent.TicketClassifier;
import com.supportorchestrator.knowledge.KnowledgeBaseRetriever;
import com.supportorchestrator.knowledge.RetrievedChunk;
import com.supportorchestrator.ticket.EscalationReason;
import com.supportorchestrator.ticket.SupportTicket;
import com.supportorchestrator.ticket.TicketRepository;
import com.supportorchestrator.ticket.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupportOrchestratorTest {

    private final TicketClassifier classifier = mock(TicketClassifier.class);
    private final KnowledgeBaseRetriever retriever = mock(KnowledgeBaseRetriever.class);
    private final SupportAgent agent = mock(SupportAgent.class);
    private final TicketRepository tickets = mock(TicketRepository.class);
    private SupportOrchestrator orchestrator;

    private static final RetrievedChunk RELEVANT =
            new RetrievedChunk("Error 1603 means ...", "installation-guide.md", 0.82);

    @BeforeEach
    void setUp() {
        orchestrator = new SupportOrchestrator(classifier, retriever, agent,
                new EscalationPolicy(TestProperties.defaults()), tickets);
        when(tickets.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(tickets.findFirstBySessionIdOrderByCreatedAtDesc(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void answersFromKnowledgeBaseAndRecordsAutoResolvedTicket() {
        when(classifier.classify(anyString()))
                .thenReturn(new TicketClassification(Category.TECHNICAL, Urgency.MEDIUM, false, "install error"));
        when(retriever.retrieve(anyString())).thenReturn(List.of(RELEVANT));
        when(agent.answer(eq("s1"), anyString(), contains("[installation-guide.md]")))
                .thenReturn("Run the installer as administrator. [installation-guide.md]");

        ChatResponse response = orchestrator.handle(new ChatRequest("s1", "c1", "I get error 1603"));

        assertThat(response.escalated()).isFalse();
        assertThat(response.answer()).contains("administrator");
        assertThat(response.sources()).containsExactly("installation-guide.md");
        SupportTicket saved = savedTicket();
        assertThat(saved.getStatus()).isEqualTo(TicketStatus.AUTO_RESOLVED);
        assertThat(saved.getCategory()).isEqualTo(Category.TECHNICAL);
        assertThat(saved.getTopRetrievalScore()).isEqualTo(0.82);
    }

    @Test
    void escalatesCriticalIssueWithoutCallingTheModel() {
        when(classifier.classify(anyString()))
                .thenReturn(new TicketClassification(Category.TECHNICAL, Urgency.CRITICAL, false, "outage"));

        ChatResponse response = orchestrator.handle(new ChatRequest("s1", null, "Production is down!"));

        assertThat(response.escalated()).isTrue();
        assertThat(response.escalationReason()).isEqualTo(EscalationReason.CRITICAL_URGENCY);
        verify(retriever, never()).retrieve(anyString());
        verify(agent, never()).answer(anyString(), anyString(), anyString());
        assertThat(savedTicket().getStatus()).isEqualTo(TicketStatus.ESCALATED);
    }

    @Test
    void escalatesWithoutCallingTheModelWhenNothingRelevantIsRetrieved() {
        when(classifier.classify(anyString()))
                .thenReturn(new TicketClassification(Category.OTHER, Urgency.LOW, false, "unrelated"));
        when(retriever.retrieve(anyString())).thenReturn(List.of(new RetrievedChunk("x", "billing-faq.md", 0.56)));

        ChatResponse response = orchestrator.handle(new ChatRequest("s1", null, "Do you sell hardware?"));

        assertThat(response.escalationReason()).isEqualTo(EscalationReason.LOW_RETRIEVAL_CONFIDENCE);
        verify(agent, never()).answer(anyString(), anyString(), anyString());
    }

    @Test
    void escalatesWhenModelCannotAnswerFromContext() {
        when(classifier.classify(anyString()))
                .thenReturn(new TicketClassification(Category.TECHNICAL, Urgency.MEDIUM, false, "?"));
        when(retriever.retrieve(anyString())).thenReturn(List.of(RELEVANT));
        when(agent.answer(anyString(), anyString(), anyString())).thenReturn(SupportAgent.NO_ANSWER);

        ChatResponse response = orchestrator.handle(new ChatRequest("s1", null, "Error 9999?"));

        assertThat(response.escalationReason()).isEqualTo(EscalationReason.NOT_IN_KNOWLEDGE_BASE);
        assertThat(response.answer()).doesNotContain(SupportAgent.NO_ANSWER).contains("support specialist");
    }

    @Test
    void escalatesWhenModelCallFails() {
        when(classifier.classify(anyString()))
                .thenReturn(new TicketClassification(Category.TECHNICAL, Urgency.MEDIUM, false, "?"));
        when(retriever.retrieve(anyString())).thenReturn(List.of(RELEVANT));
        when(agent.answer(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("timeout"));

        ChatResponse response = orchestrator.handle(new ChatRequest("s1", null, "Error 1603"));

        assertThat(response.escalationReason()).isEqualTo(EscalationReason.AGENT_ERROR);
    }

    @Test
    void fallsBackToDefaultClassificationWhenClassifierFails() {
        when(classifier.classify(anyString())).thenThrow(new RuntimeException("bad json"));
        when(retriever.retrieve(anyString())).thenReturn(List.of(RELEVANT));
        when(agent.answer(anyString(), anyString(), anyString())).thenReturn("Answer [installation-guide.md]");

        ChatResponse response = orchestrator.handle(new ChatRequest("s1", null, "Error 1603"));

        assertThat(response.category()).isEqualTo(Category.OTHER);
        assertThat(response.escalated()).isFalse();
    }

    @Test
    void expandsShortFollowUpWithPreviousQuestionInSession() {
        SupportTicket previous = new SupportTicket();
        previous.setQuestion("How do I install on Windows?");
        when(tickets.findFirstBySessionIdOrderByCreatedAtDesc("s1")).thenReturn(Optional.of(previous));
        when(classifier.classify(anyString()))
                .thenReturn(new TicketClassification(Category.HOW_TO, Urgency.LOW, false, "follow-up"));
        when(retriever.retrieve("and on mac?")).thenReturn(List.of());
        when(retriever.retrieve("How do I install on Windows?\nand on mac?")).thenReturn(List.of(RELEVANT));
        when(agent.answer(anyString(), anyString(), anyString())).thenReturn("Drag it to Applications.");

        ChatResponse response = orchestrator.handle(new ChatRequest("s1", null, "and on mac?"));

        assertThat(response.escalated()).isFalse();
    }

    private SupportTicket savedTicket() {
        ArgumentCaptor<SupportTicket> captor = ArgumentCaptor.forClass(SupportTicket.class);
        verify(tickets).save(captor.capture());
        return captor.getValue();
    }
}
