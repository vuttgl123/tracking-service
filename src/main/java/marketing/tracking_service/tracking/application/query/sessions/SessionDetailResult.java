package marketing.tracking_service.tracking.application.query.sessions;

import lombok.Builder;
import marketing.tracking_service.shared.application.Result;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Builder
public record SessionDetailResult(
        String sessionId,
        String visitorId,
        Instant startedAt,
        Instant endedAt,
        Duration duration,
        String deviceType,
        String browser,
        String os,
        String landingUrl,
        String referrerUrl,
        int totalEvents,
        int pageViews,
        List<SessionEventDto> events,
        UtmAttributionDto attribution
) implements Result {
}
