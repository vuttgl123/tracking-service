package marketing.tracking_service.tracking.inter.rest.dto;

import java.time.Instant;
import java.util.List;

public record SessionDetailResponse(
        String sessionId,
        String visitorId,
        Instant startedAt,
        Instant endedAt,
        String landingUrl,
        String referrerUrl,
        UtmView firstTouch,
        UtmView lastTouch,
        List<EventView> events
) {
    public record UtmView(String source, String medium, String campaign, String term, String content, String clickId, Instant capturedAt) {}

    public record EventView(
            long eventId,
            String eventType,
            Instant eventAt,
            String pageUrl,
            String pageTitle,
            String referrerUrl,
            String metaJson
    ) {}
}
