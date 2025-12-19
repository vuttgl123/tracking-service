package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;

@Builder
record DeviceMetricDto(
        String deviceType,
        String browser,
        String os,
        Integer sessions,
        Integer visitors,
        Integer pageViews,
        Integer events,
        Double avgSessionDuration,
        Double bounceRate,
        Double percentage
) {
}
