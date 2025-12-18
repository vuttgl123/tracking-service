package marketing.tracking_service.shared.domain;

import java.util.List;

interface AsyncDomainEventPublisher extends DomainEventPublisher {
    void publishAsync(DomainEvent event);
    void publishAllAsync(List<DomainEvent> events);
}
