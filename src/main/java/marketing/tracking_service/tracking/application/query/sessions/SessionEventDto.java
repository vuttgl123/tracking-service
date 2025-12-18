package marketing.tracking_service.tracking.application.query.sessions;

import lombok.Builder;

import java.time.Instant;

@Builder
record SessionEventDto(
        Long eventId,
        String eventType,
        Instant eventAt,
        String pageUrl,
        String pageTitle,
        Integer scrollDepth,
        Integer timeOnPage
) {
}
