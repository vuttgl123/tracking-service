package marketing.tracking_service.tracking.infrastructure.outbox;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxMessageEntity, Long> {

    @Query("""
        SELECT o FROM OutboxMessageEntity o 
        WHERE o.status = 'NEW' 
        AND o.nextAttemptAt <= :now 
        ORDER BY o.createdAt ASC
        LIMIT :limit
        """)
    List<OutboxMessageEntity> findPendingMessages(
            @Param("now") Instant now,
            @Param("limit") int limit
    );

    @Modifying
    @Query("""
        DELETE FROM OutboxMessageEntity o 
        WHERE o.status = 'SENT' 
        AND o.sentAt < :cutoff
        """)
    int deleteOldSentMessages(@Param("cutoff") Instant cutoff);
}