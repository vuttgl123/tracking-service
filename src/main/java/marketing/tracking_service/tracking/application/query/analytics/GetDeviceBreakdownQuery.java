package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;
import marketing.tracking_service.shared.application.Query;

import java.time.Instant;

@Builder
public record GetDeviceBreakdownQuery(
        Instant startDate,
        Instant endDate,
        String dimension
) implements Query {

    public GetDeviceBreakdownQuery {
        if (dimension == null) {
            dimension = "device_type";
        }
    }
}

