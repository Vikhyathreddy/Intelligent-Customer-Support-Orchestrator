package com.supportorchestrator.ticket;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TicketController {

    private final TicketRepository tickets;

    public TicketController(TicketRepository tickets) {
        this.tickets = tickets;
    }

    @GetMapping("/tickets")
    public List<SupportTicket> list(@RequestParam(required = false) TicketStatus status,
                                    @RequestParam(defaultValue = "50") int limit) {
        PageRequest page = PageRequest.of(0, Math.min(limit, 500));
        return status == null
                ? tickets.findAllByOrderByCreatedAtDesc(page)
                : tickets.findByStatusOrderByCreatedAtDesc(status, page);
    }

    @GetMapping("/tickets/{id}")
    public SupportTicket get(@PathVariable Long id) {
        return tickets.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /** Called by a human agent once they have handled an escalated ticket. */
    @PostMapping("/tickets/{id}/resolve")
    @Transactional
    public SupportTicket resolve(@PathVariable Long id, @RequestBody ResolveRequest request) {
        SupportTicket ticket = get(id);
        if (ticket.getStatus() != TicketStatus.ESCALATED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only escalated tickets can be resolved");
        }
        ticket.resolveByHuman(request.resolution());
        return ticket;
    }

    /** Deflection metrics: the share of queries the agent resolved without a human. */
    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        long total = tickets.count();
        long autoResolved = tickets.countByStatus(TicketStatus.AUTO_RESOLVED);
        long escalated = tickets.countByStatus(TicketStatus.ESCALATED)
                + tickets.countByStatus(TicketStatus.RESOLVED_BY_HUMAN);
        Map<String, Long> byCategory = new LinkedHashMap<>();
        tickets.countByCategory().forEach(c -> byCategory.put(String.valueOf(c.getCategory()), c.getTotal()));

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalQueries", total);
        metrics.put("autoResolved", autoResolved);
        metrics.put("escalatedToHuman", escalated);
        metrics.put("deflectionRate", total == 0 ? 0.0 : (double) autoResolved / total);
        metrics.put("avgAutoResolvedLatencyMs", tickets.averageAutoResolvedLatencyMs());
        metrics.put("byCategory", byCategory);
        return metrics;
    }

    public record ResolveRequest(@NotBlank String resolution) {
    }
}
