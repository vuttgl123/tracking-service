package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;

@Builder
record TrafficSourceDto(
        String source,
        String medium,
        String campaign,
        Integer sessions,
        Integer visitors,
        Integer pageViews,
        Integer events,
        Double avgSessionDuration,
        Double bounceRate
) {
}
