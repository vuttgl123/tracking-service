package marketing.tracking_service.tracking.infrastructure.outbox;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;

public interface OutboxPublisher {
    void publish(OutboxMessageEntity message) throws Exception;
}