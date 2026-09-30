package com.supportorchestrator.chat;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The pipeline every customer message goes through:
 * <ol>
 *   <li>classify (category, urgency, does the customer want a human?)</li>
 *   <li>retrieve relevant knowledge-base chunks from pgvector</li>
 *   <li>answer with the LLM, grounded in those chunks</li>
 *   <li>escalate to a human whenever any step says the bot should not answer</li>
 * </ol>
 * Every query is stored as a ticket, whether it was auto-resolved or escalated.
 */
@Service
public class SupportOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SupportOrchestrator.class);

    static final String ESCALATION_MESSAGE =
            "I've passed your request to a support specialist (ticket #%d). They'll follow up with you shortly.";

    private final TicketClassifier classifier;
    private final KnowledgeBaseRetriever retriever;
    private final SupportAgent agent;
    private final EscalationPolicy policy;
    private final TicketRepository tickets;

    public SupportOrchestrator(TicketClassifier classifier, KnowledgeBaseRetriever retriever, SupportAgent agent,
                               EscalationPolicy policy, TicketRepository tickets) {
        this.classifier = classifier;
        this.retriever = retriever;
        this.agent = agent;
        this.policy = policy;
        this.tickets = tickets;
    }

    public ChatResponse handle(ChatRequest request) {
        long start = System.currentTimeMillis();
        SupportTicket ticket = new SupportTicket();
        ticket.setSessionId(request.sessionId());
        ticket.setCustomerId(request.customerId());
        ticket.setQuestion(request.message());

        TicketClassification classification = classify(request.message());
        ticket.setCategory(classification.category());
        ticket.setUrgency(classification.urgency());
        ticket.setSummary(classification.summary());

        List<RetrievedChunk> chunks = List.of();
        String answer = null;
        Optional<EscalationReason> escalation = policy.beforeRetrieval(classification);

        if (escalation.isEmpty()) {
            chunks = retrieve(request);
            escalation = policy.beforeAnswer(chunks);
        }
        if (escalation.isEmpty()) {
            try {
                answer = agent.answer(request.sessionId(), request.message(),
                        KnowledgeBaseRetriever.toPromptContext(chunks));
                escalation = policy.afterAnswer(answer);
            } catch (RuntimeException e) {
                log.error("Agent failed to answer for session {}", request.sessionId(), e);
                escalation = Optional.of(EscalationReason.AGENT_ERROR);
            }
        }

        List<String> sources = chunks.stream().map(RetrievedChunk::source).distinct().toList();
        ticket.setSources(String.join(",", sources));
        ticket.setTopRetrievalScore(chunks.isEmpty() ? null : chunks.getFirst().score());
        ticket.setStatus(escalation.isPresent() ? TicketStatus.ESCALATED : TicketStatus.AUTO_RESOLVED);
        ticket.setEscalationReason(escalation.orElse(null));
        ticket.setAnswer(escalation.isPresent() ? null : answer);
        ticket.setLatencyMs(System.currentTimeMillis() - start);
        tickets.save(ticket);

        String reply = escalation.isPresent() ? ESCALATION_MESSAGE.formatted(ticket.getId()) : answer;
        log.info("Ticket {} category={} urgency={} status={} reason={} latencyMs={}", ticket.getId(),
                ticket.getCategory(), ticket.getUrgency(), ticket.getStatus(), ticket.getEscalationReason(),
                ticket.getLatencyMs());
        return new ChatResponse(ticket.getId(), reply, escalation.isPresent(), escalation.orElse(null),
                classification.category(), classification.urgency(), sources);
    }

    private TicketClassification classify(String message) {
        try {
            TicketClassification result = classifier.classify(message);
            if (result != null && result.category() != null && result.urgency() != null) {
                return result;
            }
        } catch (RuntimeException e) {
            log.warn("Classification failed, falling back to defaults: {}", e.getMessage());
        }
        return new TicketClassification(Category.OTHER, Urgency.MEDIUM, false, null);
    }

    /**
     * Retrieves on the message alone first. Short follow-ups ("what about on Windows?") often carry too
     * little meaning to retrieve on, so when nothing relevant comes back the previous question in the
     * session is added to the query.
     */
    private List<RetrievedChunk> retrieve(ChatRequest request) {
        List<RetrievedChunk> chunks = retriever.retrieve(request.message());
        if (policy.beforeAnswer(chunks).isEmpty()) {
            return chunks;
        }
        return tickets.findFirstBySessionIdOrderByCreatedAtDesc(request.sessionId())
                .map(previous -> retriever.retrieve(previous.getQuestion() + "\n" + request.message()))
                .filter(expanded -> !expanded.isEmpty())
                .orElse(chunks);
    }
}
