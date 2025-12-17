package marketing.tracking_service.tracking.domain.event;

import marketing.tracking_service.shared.domain.DomainEvent;
import marketing.tracking_service.shared.domain.PublicEvent;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record EventTracked(
        String eventId,
        Long trackingEventId,
        String sessionId,
        String visitorId,
        String clientEventId,
        String eventType,
        Instant eventAt,
        String pageUrl,
        String pageTitle,
        Map<String, Object> metadata,
        Instant occurredAt
) implements DomainEvent, PublicEvent {

    public EventTracked {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        Objects.requireNonNull(visitorId, "Visitor ID cannot be null");
        Objects.requireNonNull(clientEventId, "Client event ID cannot be null");
        Objects.requireNonNull(eventType, "Event type cannot be null");
        Objects.requireNonNull(eventAt, "Event at cannot be null");

        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    public static EventTracked of(
            Long trackingEventId,
            String sessionId,
            String visitorId,
            String clientEventId,
            String eventType,
            Instant eventAt,
            String pageUrl,
            String pageTitle,
            Map<String, Object> metadata
    ) {
        return new EventTracked(
                null,
                trackingEventId,
                sessionId,
                visitorId,
                clientEventId,
                eventType,
                eventAt,
                pageUrl,
                pageTitle,
                metadata,
                null
        );
    }

    @Override
    public String eventType() {
        return "EventTracked";
    }

    @Override
    public String aggregateId() {
        return trackingEventId != null ? trackingEventId.toString() : clientEventId;
    }

    @Override
    public String aggregateType() {
        return "TrackingEvent";
    }
}