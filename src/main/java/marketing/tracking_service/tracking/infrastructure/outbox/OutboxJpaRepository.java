package marketing.tracking_service.tracking.infrastructure.outbox;

import jakarta.persistence.LockModeType;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxMessageEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select o from OutboxMessageEntity o
    where (o.status = 'NEW' or o.status = 'FAILED')
      and o.nextAttemptAt <= :now
    order by o.outboxId asc
  """)
    List<OutboxMessageEntity> lockBatchDue(@Param("now") Instant now, Pageable pageable);
}