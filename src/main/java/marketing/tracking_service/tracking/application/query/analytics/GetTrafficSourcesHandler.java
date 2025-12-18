package marketing.tracking_service.tracking.application.query.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class GetTrafficSourcesHandler {

    private final TrafficSourcesQueryService queryService;

    @Transactional(readOnly = true)
    public TrafficSourcesResult handle(GetTrafficSourcesQuery query) {
        Instant startDate = query.startDate() != null
                ? query.startDate()
                : Instant.now().minus(30, ChronoUnit.DAYS);

        Instant endDate = query.endDate() != null
                ? query.endDate()
                : Instant.now();

        return queryService.getTrafficSources(
                startDate,
                endDate,
                query.groupBy(),
                query.limit()
        );
    }
}