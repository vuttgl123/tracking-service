package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;

@Builder
record DeviceDistributionDto(
        Double mobilePercentage,
        Double tabletPercentage,
        Double desktopPercentage,
        Double botPercentage
) {
}
