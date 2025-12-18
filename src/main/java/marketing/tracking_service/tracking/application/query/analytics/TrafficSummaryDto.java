package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;

@Builder
record TrafficSummaryDto(
        Integer totalSessions,
        Integer totalVisitors,
        Integer totalPageViews,
        Integer totalEvents,
        Double avgSessionDuration
) {
}
