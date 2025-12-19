package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;

@Builder
record DevicePerformanceDto(
        String topDevice,
        String topBrowser,
        String topOs,
        Double avgMobileDuration,
        Double avgDesktopDuration
) {
}
