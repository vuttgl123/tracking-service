package marketing.tracking_service.tracking.application.query.sessions;

import marketing.tracking_service.tracking.inter.rest.dto.SessionDetailResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class SessionDetailQueryService {

    private final JdbcTemplate jdbc;

    public SessionDetailQueryService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public SessionDetailResponse getBySessionId(String sessionId, int limit) {
        Map<String, Object> s = jdbc.queryForMap("""
        SELECT session_id, visitor_id, started_at, ended_at, landing_url, referrer_url
        FROM sessions
        WHERE session_id = ?
      """, sessionId);

        String visitorId = (String) s.get("visitor_id");

        SessionDetailResponse.UtmView first = fetchUtm(sessionId, "first");
        SessionDetailResponse.UtmView last = fetchUtm(sessionId, "last");

        List<SessionDetailResponse.EventView> events = jdbc.query("""
        SELECT event_id, event_type, event_at, page_url, page_title, referrer_url, CAST(meta AS CHAR) AS meta_json
        FROM events
        WHERE session_id = ?
        ORDER BY event_at DESC, event_id DESC
        LIMIT ?
      """,
                (rs, i) -> new SessionDetailResponse.EventView(
                        rs.getLong("event_id"),
                        rs.getString("event_type"),
                        rs.getTimestamp("event_at").toInstant(),
                        rs.getString("page_url"),
                        rs.getString("page_title"),
                        rs.getString("referrer_url"),
                        rs.getString("meta_json")
                ),
                sessionId, limit
        );

        return new SessionDetailResponse(
                (String) s.get("session_id"),
                visitorId,
                toInstant(s.get("started_at")),
                toInstant(s.get("ended_at")),
                (String) s.get("landing_url"),
                (String) s.get("referrer_url"),
                first,
                last,
                events
        );
    }

    private SessionDetailResponse.UtmView fetchUtm(String sessionId, String touchType) {
        List<SessionDetailResponse.UtmView> list = jdbc.query("""
        SELECT utm_source, utm_medium, utm_campaign, utm_term, utm_content, click_id, captured_at
        FROM utm_attributions
        WHERE session_id = ? AND touch_type = ?
        LIMIT 1
      """,
                (rs, i) -> new SessionDetailResponse.UtmView(
                        rs.getString("utm_source"),
                        rs.getString("utm_medium"),
                        rs.getString("utm_campaign"),
                        rs.getString("utm_term"),
                        rs.getString("utm_content"),
                        rs.getString("click_id"),
                        rs.getTimestamp("captured_at").toInstant()
                ),
                sessionId, touchType
        );
        return list.isEmpty() ? null : list.get(0);
    }

    private static Instant toInstant(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp ts) return ts.toInstant();
        return null;
    }
}
