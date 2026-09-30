package com.supportorchestrator.ticket;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByStatusOrderByCreatedAtDesc(TicketStatus status, Pageable pageable);

    List<SupportTicket> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<SupportTicket> findFirstBySessionIdOrderByCreatedAtDesc(String sessionId);

    long countByStatus(TicketStatus status);

    @Query("select avg(t.latencyMs) from SupportTicket t where t.status = com.supportorchestrator.ticket.TicketStatus.AUTO_RESOLVED")
    Double averageAutoResolvedLatencyMs();

    @Query("select t.category as category, count(t) as total from SupportTicket t group by t.category")
    List<CategoryCount> countByCategory();

    interface CategoryCount {
        String getCategory();

        long getTotal();
    }
}
