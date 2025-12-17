package marketing.tracking_service.shared.domain;

import java.util.List;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
    default void publishFrom(AggregateRoot<?> aggregate) {
        if (aggregate.hasDomainEvents()) {
            publishAll(aggregate.getDomainEvents());
            aggregate.clearDomainEvents();
        }
    }
}

