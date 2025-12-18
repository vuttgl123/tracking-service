package marketing.tracking_service.shared.domain;

import java.time.Instant;
import java.util.UUID;

abstract class AbstractDomainEvent implements DomainEvent {
    private final String eventId;
    private final Instant occurredAt;
    private final String version;

    protected AbstractDomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.version = "1.0";
    }

    protected AbstractDomainEvent(String version) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.version = version;
    }

    @Override
    public String eventId() {
        return eventId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String eventType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, occurredAt=%s]", eventType(), eventId, occurredAt);
    }
}
