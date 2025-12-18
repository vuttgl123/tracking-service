package marketing.tracking_service.tracking.inter.rest;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.application.query.analytics.GetTrafficSourcesHandler;
import marketing.tracking_service.tracking.application.query.analytics.GetTrafficSourcesQuery;
import marketing.tracking_service.tracking.application.query.analytics.TrafficSourcesResult;
import marketing.tracking_service.tracking.application.query.sessions.GetSessionDetailHandler;
import marketing.tracking_service.tracking.application.query.sessions.GetSessionDetailQuery;
import marketing.tracking_service.tracking.application.query.sessions.SessionDetailResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class SessionQueryController {

    private final GetSessionDetailHandler sessionDetailHandler;
    private final GetTrafficSourcesHandler trafficSourcesHandler;

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDetailResult> getSessionDetail(@PathVariable String sessionId) {
        GetSessionDetailQuery query = GetSessionDetailQuery.builder().sessionId(sessionId).build();
        SessionDetailResult result = sessionDetailHandler.handle(query);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/traffic-sources")
    public ResponseEntity<TrafficSourcesResult> getTrafficSources(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(required = false, defaultValue = "source") String groupBy,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        GetTrafficSourcesQuery query = GetTrafficSourcesQuery.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy(groupBy)
                .limit(limit)
                .build();

        TrafficSourcesResult result = trafficSourcesHandler.handle(query);

        return ResponseEntity.ok(result);
    }
}
