package marketing.tracking_service.tracking.infrastructure.outbox;

import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogOutboxPublisher implements OutboxPublisher {
    @Override
    public void publish(OutboxMessageEntity message) {
        log.info("Publishing domain event: type={}, aggregateType={}, aggregateId={}", message.eventType, message.aggregateType, message.aggregateId);
        log.debug("Event payload: {}", message.payloadJson);
    }
}