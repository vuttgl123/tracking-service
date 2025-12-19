package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;

@Builder
record DeviceComparisonDto(
        Integer totalSessions,
        DeviceDistributionDto distribution,
        DevicePerformanceDto performance
) {
}
