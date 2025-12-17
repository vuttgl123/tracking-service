package marketing.tracking_service.tracking.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import marketing.tracking_service.tracking.domain.model.event.*;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.EventEntity;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.EventTypeEnum;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrackingEventMapper {

    private final ObjectMapper objectMapper;

    public TrackingEventMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public EventEntity toEntity(TrackingEvent event) {
        return EventEntity.builder()
                .eventId(event.getId())
                .sessionId(event.getSessionId().value())
                .visitorId(event.getVisitorId().value())
                .clientEventId(event.getClientEventId())
                .eventType(EventTypeEnum.valueOf(event.getEventType().name()))
                .eventAt(event.getEventAt())
                .pageUrl(event.getPageContext().pageUrl())
                .pageTitle(event.getPageContext().pageTitle())
                .referrerUrl(event.getPageContext().referrerUrl())
                .scrollDepth(event.getInteractionContext().scrollDepth())
                .timeOnPage(event.getInteractionContext().timeOnPage())
                .viewportWidth(event.getInteractionContext().viewport() != null
                        ? event.getInteractionContext().viewport().width() : null)
                .viewportHeight(event.getInteractionContext().viewport() != null
                        ? event.getInteractionContext().viewport().height() : null)
                .elementId(event.getInteractionContext().element() != null
                        ? event.getInteractionContext().element().elementId() : null)
                .elementClass(event.getInteractionContext().element() != null
                        ? event.getInteractionContext().element().elementClass() : null)
                .elementText(event.getInteractionContext().element() != null
                        ? event.getInteractionContext().element().elementText() : null)
                .metaJson(serializeMetadata(event.getMetadata()))
                .build();
    }

    public TrackingEvent toDomain(EventEntity entity) {
        PageContext pageContext = PageContext.builder()
                .pageUrl(entity.getPageUrl())
                .pageTitle(entity.getPageTitle())
                .referrerUrl(entity.getReferrerUrl())
                .build();

        InteractionContext interactionContext = InteractionContext.builder()
                .scrollDepth(entity.getScrollDepth())
                .timeOnPage(entity.getTimeOnPage())
                .viewport(entity.getViewportWidth() != null && entity.getViewportHeight() != null
                        ? new ViewportSize(entity.getViewportWidth(), entity.getViewportHeight())
                        : null)
                .element(entity.getElementId() != null || entity.getElementClass() != null
                        ? new ElementInfo(entity.getElementId(), entity.getElementClass(), entity.getElementText())
                        : null)
                .build();

        TrackingEvent event = TrackingEvent.track(
                SessionId.from(entity.getSessionId()),
                VisitorId.from(entity.getVisitorId()),
                entity.getClientEventId(),
                TrackingEventType.valueOf(entity.getEventType().name()),
                entity.getEventAt(),
                pageContext,
                interactionContext,
                deserializeMetadata(entity.getMetaJson())
        );

        if (entity.getEventId() != null) {
            event.markAsPersisted(entity.getEventId());
            event.clearDomainEvents();
        }

        return event;
    }

    private String serializeMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize metadata", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deserializeMetadata(String metaJson) {
        if (metaJson == null || metaJson.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(metaJson, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize metadata", e);
        }
    }
}