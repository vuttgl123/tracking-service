package marketing.tracking_service.tracking.application.query.analytics;

import lombok.Builder;
import marketing.tracking_service.shared.application.Query;

import java.time.Instant;

@Builder
public record GetTrafficSourcesQuery(
        Instant startDate,
        Instant endDate,
        String groupBy,
        Integer limit
) implements Query {

    public GetTrafficSourcesQuery {
        if (groupBy == null) {
            groupBy = "source";
        }
        if (limit == null || limit <= 0) {
            limit = 10;
        }
    }
}

