package marketing.tracking_service.tracking.domain.event;

import marketing.tracking_service.shared.domain.DomainEvent;
import marketing.tracking_service.shared.domain.PublicEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record VisitorFirstSeen(
        String eventId,
        String visitorId,
        Instant firstSeenAt,
        String firstReferrer,
        String firstLandingPage,
        String userAgent,
        Instant occurredAt
) implements DomainEvent, PublicEvent {

    public VisitorFirstSeen {
        Objects.requireNonNull(visitorId, "Visitor ID cannot be null");
        Objects.requireNonNull(firstSeenAt, "First seen at cannot be null");

        if (eventId == null) {
            eventId = UUID.randomUUID().toString();
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    public static VisitorFirstSeen of(
            String visitorId,
            Instant firstSeenAt,
            String firstReferrer,
            String firstLandingPage,
            String userAgent
    ) {
        return new VisitorFirstSeen(
                null,
                visitorId,
                firstSeenAt,
                firstReferrer,
                firstLandingPage,
                userAgent,
                null
        );
    }

    @Override
    public String eventType() {
        return "VisitorFirstSeen";
    }

    @Override
    public String aggregateId() {
        return visitorId;
    }

    @Override
    public String aggregateType() {
        return "Visitor";
    }
}