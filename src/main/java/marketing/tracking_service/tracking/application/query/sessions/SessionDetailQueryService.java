package marketing.tracking_service.tracking.application.query.sessions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionDetailQueryService {

    @PersistenceContext
    private EntityManager em;

    public SessionDetailResult getSessionDetail(SessionId sessionId) {
        String sql = """
            SELECT 
                s.session_id,
                s.visitor_id,
                s.started_at,
                s.ended_at,
                s.device_type,
                s.browser,
                s.os,
                s.landing_url,
                s.referrer_url,
                COUNT(DISTINCT e.event_id) as total_events,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views
            FROM sessions s
            LEFT JOIN events e ON s.session_id = e.session_id
            WHERE s.session_id = ?1
            GROUP BY s.session_id
            """;

        Object[] row = (Object[]) em.createNativeQuery(sql)
                .setParameter(1, sessionId.value())
                .getSingleResult();

        String sid = (String) row[0];
        String vid = (String) row[1];
        Instant startedAt = toInstant(row[2]);
        Instant endedAt   = toInstant(row[3]);
        String deviceType = (String) row[4];
        String browser = (String) row[5];
        String os = (String) row[6];
        String landingUrl = (String) row[7];
        String referrerUrl = (String) row[8];
        Long totalEvents = ((Number) row[9]).longValue();
        Long pageViews = ((Number) row[10]).longValue();

        Duration duration = endedAt != null
                ? Duration.between(startedAt, endedAt)
                : Duration.between(startedAt, Instant.now());

        List<SessionEventDto> events = getSessionEvents(sessionId);
        UtmAttributionDto attribution = getUtmAttribution(sessionId);

        return SessionDetailResult.builder()
                .sessionId(sid)
                .visitorId(vid)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .duration(duration)
                .deviceType(deviceType)
                .browser(browser)
                .os(os)
                .landingUrl(landingUrl)
                .referrerUrl(referrerUrl)
                .totalEvents(totalEvents.intValue())
                .pageViews(pageViews.intValue())
                .events(events)
                .attribution(attribution)
                .build();
    }

    private List<SessionEventDto> getSessionEvents(SessionId sessionId) {
        String sql = """
            SELECT 
                event_id, event_type, event_at, page_url, page_title, scroll_depth, time_on_page
            FROM events
            WHERE session_id = ?1
            ORDER BY event_at ASC
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter(1, sessionId.value())
                .getResultList();

        return results.stream()
                .map(row -> SessionEventDto.builder()
                        .eventId(((Number) row[0]).longValue())
                        .eventType((String) row[1])
                        .eventAt(toInstant(row[2]))
                        .pageUrl((String) row[3])
                        .pageTitle((String) row[4])
                        .scrollDepth(row[5] != null ? ((Number) row[5]).intValue() : null)
                        .timeOnPage(row[6] != null ? ((Number) row[6]).intValue() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private UtmAttributionDto getUtmAttribution(SessionId sessionId) {
        String sql = """
            SELECT utm_source, utm_medium, utm_campaign, utm_content, utm_term
            FROM utm_attributions
            WHERE session_id = ?1 AND touch_type = 'LAST'
            LIMIT 1
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter(1, sessionId.value())
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }

        Object[] row = results.get(0);
        return UtmAttributionDto.builder()
                .source((String) row[0])
                .medium((String) row[1])
                .campaign((String) row[2])
                .content((String) row[3])
                .term((String) row[4])
                .build();
    }

    private Instant toInstant(Object dbValue) {
        if (dbValue == null) return null;

        if (dbValue instanceof Timestamp ts) {
            return ts.toInstant();
        }
        if (dbValue instanceof LocalDateTime ldt) {
            return ldt.toInstant(ZoneOffset.UTC);
        }
        throw new IllegalArgumentException("Unsupported datetime type: " + dbValue.getClass());
    }

}

