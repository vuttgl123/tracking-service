package marketing.tracking_service.tracking.infrastructure.outbox;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class OutboxService {

    private final OutboxJpaRepository repo;
    private final Clock clock;

    public OutboxService(OutboxJpaRepository repo, Clock clock) {
        this.repo = repo;
        this.clock = clock;
    }

    @Transactional
    public void enqueue(String aggregateType, String aggregateId, String eventType, String payloadJson) {
        Instant now = clock.instant();

        OutboxMessageEntity o = new OutboxMessageEntity();
        o.aggregateType = aggregateType;
        o.aggregateId = aggregateId;
        o.eventType = eventType;
        o.payloadJson = payloadJson;
        o.status = OutboxStatus.NEW;
        o.retryCount = 0;
        o.nextAttemptAt = now;
        o.createdAt = now;

        repo.save(o);
    }

    public Instant now() {
        return clock.instant();
    }
}
