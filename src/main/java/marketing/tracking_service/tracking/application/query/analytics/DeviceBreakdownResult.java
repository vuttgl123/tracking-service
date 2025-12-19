package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;
import marketing.tracking_service.shared.application.Result;

import java.time.Instant;
import java.util.List;

@Builder
public record DeviceBreakdownResult(
        Instant startDate,
        Instant endDate,
        String dimension,
        List<DeviceMetricDto> devices,
        DeviceComparisonDto comparison
) implements Result {
}
