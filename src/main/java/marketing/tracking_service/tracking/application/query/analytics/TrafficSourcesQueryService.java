package marketing.tracking_service.tracking.application.query.analytics;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrafficSourcesQueryService {

    @PersistenceContext
    private EntityManager em;

    public TrafficSourcesResult getTrafficSources(
            Instant startDate,
            Instant endDate,
            String groupBy,
            Integer limit
    ) {
        List<TrafficSourceDto> sources = switch (groupBy.toLowerCase()) {
            case "source" -> getBySource(startDate, endDate, limit);
            case "medium" -> getByMedium(startDate, endDate, limit);
            case "campaign" -> getByCampaign(startDate, endDate, limit);
            case "source_medium" -> getBySourceMedium(startDate, endDate, limit);
            default -> getBySource(startDate, endDate, limit);
        };

        TrafficSummaryDto summary = getSummary(startDate, endDate);

        return TrafficSourcesResult.builder()
                .startDate(startDate)
                .endDate(endDate)
                .sources(sources)
                .summary(summary)
                .build();
    }

    private List<TrafficSourceDto> getBySource(Instant startDate, Instant endDate, Integer limit) {
        String sql = """
            SELECT 
                COALESCE(u.utm_source, 'direct') as source,
                NULL as medium,
                NULL as campaign,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW()))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            LEFT JOIN utm_attributions u ON s.session_id = u.session_id AND u.touch_type = 'LAST'
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY source
            ORDER BY sessions DESC
            LIMIT ?3
            """;

        return executeTrafficQuery(sql, startDate, endDate, limit);
    }

    private List<TrafficSourceDto> getByMedium(Instant startDate, Instant endDate, Integer limit) {
        String sql = """
            SELECT 
                NULL as source,
                COALESCE(u.utm_medium, 'none') as medium,
                NULL as campaign,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            LEFT JOIN utm_attributions u ON s.session_id = u.session_id AND u.touch_type = 'LAST'
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY medium
            ORDER BY sessions DESC
            LIMIT ?3
            """;

        return executeTrafficQuery(sql, startDate, endDate, limit);
    }

    private List<TrafficSourceDto> getByCampaign(Instant startDate, Instant endDate, Integer limit) {
        String sql = """
            SELECT 
                u.utm_source as source,
                u.utm_medium as medium,
                u.utm_campaign as campaign,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            INNER JOIN utm_attributions u ON s.session_id = u.session_id AND u.touch_type = 'LAST'
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
                AND u.utm_campaign IS NOT NULL
            GROUP BY source, medium, campaign
            ORDER BY sessions DESC
            LIMIT ?3
            """;

        return executeTrafficQuery(sql, startDate, endDate, limit);
    }

    private List<TrafficSourceDto> getBySourceMedium(Instant startDate, Instant endDate, Integer limit) {
        String sql = """
            SELECT 
                COALESCE(u.utm_source, 'direct') as source,
                COALESCE(u.utm_medium, 'none') as medium,
                NULL as campaign,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            LEFT JOIN utm_attributions u ON s.session_id = u.session_id AND u.touch_type = 'LAST'
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY source, medium
            ORDER BY sessions DESC
            LIMIT ?3
            """;

        return executeTrafficQuery(sql, startDate, endDate, limit);
    }

    @SuppressWarnings("unchecked")
    private List<TrafficSourceDto> executeTrafficQuery(String sql, Instant startDate, Instant endDate, Integer limit) {
        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter(1, java.sql.Timestamp.from(startDate))
                .setParameter(2, java.sql.Timestamp.from(endDate))
                .setParameter(3, limit)
                .getResultList();

        List<TrafficSourceDto> sources = new ArrayList<>();
        for (Object[] row : results) {
            sources.add(TrafficSourceDto.builder()
                    .source((String) row[0])
                    .medium((String) row[1])
                    .campaign((String) row[2])
                    .sessions(row[3] != null ? ((Number) row[3]).intValue() : 0)
                    .visitors(row[4] != null ? ((Number) row[4]).intValue() : 0)
                    .pageViews(row[5] != null ? ((Number) row[5]).intValue() : 0)
                    .events(row[6] != null ? ((Number) row[6]).intValue() : 0)
                    .avgSessionDuration(row[7] != null ? ((Number) row[7]).doubleValue() : 0.0)
                    .bounceRate(row[8] != null ? ((Number) row[8]).doubleValue() : 0.0)
                    .build());
        }

        return sources;
    }

    private TrafficSummaryDto getSummary(Instant startDate, Instant endDate) {
        String sql = """
            SELECT 
                COUNT(DISTINCT s.session_id) as total_sessions,
                COUNT(DISTINCT s.visitor_id) as total_visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as total_page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW()))) as avg_duration
            FROM sessions s
            LEFT JOIN events e ON s.session_id = e.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            """;

        Object[] row = (Object[]) em.createNativeQuery(sql)
                .setParameter(1, java.sql.Timestamp.from(startDate))
                .setParameter(2, java.sql.Timestamp.from(endDate))
                .getSingleResult();

        return TrafficSummaryDto.builder()
                .totalSessions(row[0] != null ? ((Number) row[0]).intValue() : 0)
                .totalVisitors(row[1] != null ? ((Number) row[1]).intValue() : 0)
                .totalPageViews(row[2] != null ? ((Number) row[2]).intValue() : 0)
                .totalEvents(row[3] != null ? ((Number) row[3]).intValue() : 0)
                .avgSessionDuration(row[4] != null ? ((Number) row[4]).doubleValue() : 0.0)
                .build();
    }
}