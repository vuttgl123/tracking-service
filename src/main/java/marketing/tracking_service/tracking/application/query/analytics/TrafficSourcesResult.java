package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;
import marketing.tracking_service.shared.application.Result;

import java.time.Instant;
import java.util.List;

@Builder
public record TrafficSourcesResult(
        Instant startDate,
        Instant endDate,
        List<TrafficSourceDto> sources,
        TrafficSummaryDto summary
) implements Result {
}
