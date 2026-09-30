package com.supportorchestrator.ticket;

import com.supportorchestrator.agent.TicketClassification.Category;
import com.supportorchestrator.agent.TicketClassification.Urgency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * One customer query and how it was handled. Every query is recorded, including the ones the agent
 * resolved on its own, so deflection rate and answer quality can be measured.
 */
@Entity
@Table(name = "support_ticket")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String customerId;

    @Column(columnDefinition = "text")
    private String question;

    @Column(columnDefinition = "text")
    private String answer;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Urgency urgency;

    private String summary;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private EscalationReason escalationReason;

    private Double topRetrievalScore;
    private String sources;
    private Long latencyMs;

    @Column(columnDefinition = "text")
    private String resolution;

    private Instant createdAt = Instant.now();
    private Instant resolvedAt;

    public void resolveByHuman(String resolution) {
        this.status = TicketStatus.RESOLVED_BY_HUMAN;
        this.resolution = resolution;
        this.resolvedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Urgency getUrgency() { return urgency; }
    public void setUrgency(Urgency urgency) { this.urgency = urgency; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public EscalationReason getEscalationReason() { return escalationReason; }
    public void setEscalationReason(EscalationReason escalationReason) { this.escalationReason = escalationReason; }
    public Double getTopRetrievalScore() { return topRetrievalScore; }
    public void setTopRetrievalScore(Double topRetrievalScore) { this.topRetrievalScore = topRetrievalScore; }
    public String getSources() { return sources; }
    public void setSources(String sources) { this.sources = sources; }
    public Long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Long latencyMs) { this.latencyMs = latencyMs; }
    public String getResolution() { return resolution; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
}
