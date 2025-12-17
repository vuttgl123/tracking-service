package marketing.tracking_service.tracking.inter.rest.dto;

import marketing.tracking_service.tracking.domain.model.event.TrackingEventType;

public record TrackEventRequest(
        String visitorId,
        String sessionId,
        String clientEventId,
        TrackingEventType eventType,
        String ipHash,
        String userAgent,
        String pageUrl,
        String pageTitle,
        String referrerUrl,
        String landingUrl,
        UtmDto utm,
        Object meta

) {
    public record UtmDto(
            String source,
            String medium,
            String campaign,
            String term,
            String content,
            String clickId
    ) {}
}
