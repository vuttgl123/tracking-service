package marketing.tracking_service.tracking.domain.model.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import marketing.tracking_service.shared.domain.AggregateRoot;
import marketing.tracking_service.tracking.domain.event.EventTracked;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackingEvent extends AggregateRoot<Long> {
    private SessionId sessionId;
    private VisitorId visitorId;
    private String clientEventId;
    private TrackingEventType eventType;
    private Instant eventAt;
    private PageContext pageContext;
    private InteractionContext interactionContext;
    private Map<String, Object> metadata;

    private TrackingEvent(SessionId sessionId, VisitorId visitorId, String clientEventId, TrackingEventType eventType, Instant eventAt, PageContext pageContext, InteractionContext interactionContext, Map<String, Object> metadata) {
        super();
        this.sessionId = Objects.requireNonNull(sessionId);
        this.visitorId = Objects.requireNonNull(visitorId);
        this.clientEventId = clientEventId != null ? clientEventId : UUID.randomUUID().toString();
        this.eventType = Objects.requireNonNull(eventType);
        this.eventAt = Objects.requireNonNull(eventAt);
        this.pageContext = Objects.requireNonNull(pageContext);
        this.interactionContext = interactionContext != null ? interactionContext : InteractionContext.empty();
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    public static TrackingEvent track(SessionId sessionId, VisitorId visitorId, String clientEventId, TrackingEventType eventType, Instant eventAt, PageContext pageContext, InteractionContext interactionContext, Map<String, Object> metadata) {
        return new TrackingEvent(sessionId, visitorId, clientEventId, eventType, eventAt, pageContext, interactionContext, metadata);
    }

    public void markAsPersisted(Long id) {
        if (isPersisted()) {
            throw new IllegalStateException("Event already persisted");
        }
        setId(id);
        registerEvent(EventTracked.of(id, sessionId.value(), visitorId.value(), clientEventId, eventType.name(), eventAt, pageContext.pageUrl(), pageContext.pageTitle(), metadata));
    }

    public boolean isPageView() {
        return eventType.isPageView();
    }

    public boolean isInteraction() {
        return eventType.isInteraction();
    }

    public boolean isConversion() {
        return eventType.isConversion();
    }

    @Override
    public void validate() {
        if (sessionId == null) {
            throw new IllegalStateException("Session ID is required");
        }
        if (visitorId == null) {
            throw new IllegalStateException("Visitor ID is required");
        }
        if (clientEventId == null || clientEventId.isBlank()) {
            throw new IllegalStateException("Client event ID is required");
        }
        if (eventType == null) {
            throw new IllegalStateException("Event type is required");
        }
        if (eventAt == null) {
            throw new IllegalStateException("Event at is required");
        }
        if (pageContext == null) {
            throw new IllegalStateException("Page context is required");
        }
    }
}