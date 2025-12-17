package marketing.tracking_service.tracking.application.command.trackevent;

import lombok.Builder;
import marketing.tracking_service.shared.application.Command;
import marketing.tracking_service.tracking.domain.model.event.TrackingEventType;

import java.time.Instant;
import java.util.Map;

@Builder
public record TrackEventCommand(
        String visitorId,
        String sessionId,
        String clientEventId,
        TrackingEventType eventType,
        Instant eventAt,
        String ipAddress,
        String userAgent,
        Integer screenWidth,
        Integer screenHeight,
        String language,
        String timezone,
        String pageUrl,
        String pageTitle,
        String referrerUrl,
        String landingUrl,
        Integer scrollDepth,
        Integer timeOnPage,
        Integer viewportWidth,
        Integer viewportHeight,
        String elementId,
        String elementClass,
        String elementText,
        Map<String, Object> metadata,
        UtmParameters utm
) implements Command {

    public boolean hasUtm() {
        return utm != null && utm.hasAnyParameter();
    }
}

