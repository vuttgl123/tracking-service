package marketing.tracking_service.tracking.infrastructure.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.shared.domain.DomainEvent;
import marketing.tracking_service.shared.domain.DomainEventPublisher;
import marketing.tracking_service.tracking.infrastructure.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void publish(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            outboxService.enqueue(
                    event.aggregateType(),
                    event.aggregateId(),
                    event.eventType(),
                    payload
            );

            log.debug("Domain event published to outbox: type={}, aggregateId={}",
                    event.eventType(), event.aggregateId());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize domain event: {}", event.eventType(), e);
            throw new RuntimeException("Failed to publish domain event", e);
        }
    }

    @Override
    @Transactional
    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DomainEvent event : events) {
            publish(event);
        }

        log.debug("Published {} domain events to outbox", events.size());
    }
}