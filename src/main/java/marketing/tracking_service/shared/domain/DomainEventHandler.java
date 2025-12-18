package marketing.tracking_service.shared.domain;

@FunctionalInterface
interface DomainEventHandler<T extends DomainEvent> {
    void handle(T event);
    default Class<T> getEventType() {
        throw new UnsupportedOperationException("Event type must be specified");
    }
}
