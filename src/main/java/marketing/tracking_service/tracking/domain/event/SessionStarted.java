package marketing.tracking_service.tracking.domain.event;

import marketing.tracking_service.shared.domain.DomainEvent;
import marketing.tracking_service.shared.domain.PublicEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SessionStarted(
        String eventId,
        String sessionId,
        String visitorId,
        Instant startedAt,
        String ipHash,
        String userAgent,
        String referrerUrl,
        String landingUrl,
        Instant occurredAt
) implements DomainEvent, PublicEvent {

    public SessionStarted {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        Objects.requireNonNull(visitorId, "Visitor ID cannot be null");
        Objects.requireNonNull(startedAt, "Started at cannot be null");

        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    public static SessionStarted of(
            String sessionId,
            String visitorId,
            Instant startedAt,
            String ipHash,
            String userAgent,
            String referrerUrl,
            String landingUrl
    ) {
        return new SessionStarted(
                null,
                sessionId,
                visitorId,
                startedAt,
                ipHash,
                userAgent,
                referrerUrl,
                landingUrl,
                null
        );
    }

    @Override
    public String eventType() {
        return "SessionStarted";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public String aggregateType() {
        return "Session";
    }
}