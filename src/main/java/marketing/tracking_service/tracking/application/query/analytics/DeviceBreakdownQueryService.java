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
public class DeviceBreakdownQueryService {

    @PersistenceContext
    private EntityManager em;

    public DeviceBreakdownResult getDeviceBreakdown(
            Instant startDate,
            Instant endDate,
            String dimension
    ) {
        List<DeviceMetricDto> devices = switch (dimension.toLowerCase()) {
            case "device_type" -> getByDeviceType(startDate, endDate);
            case "browser" -> getByBrowser(startDate, endDate);
            case "os" -> getByOs(startDate, endDate);
            case "device_browser" -> getByDeviceAndBrowser(startDate, endDate);
            default -> getByDeviceType(startDate, endDate);
        };

        DeviceComparisonDto comparison = getComparison(startDate, endDate);

        return DeviceBreakdownResult.builder()
                .startDate(startDate)
                .endDate(endDate)
                .dimension(dimension)
                .devices(devices)
                .comparison(comparison)
                .build();
    }

    private List<DeviceMetricDto> getByDeviceType(Instant startDate, Instant endDate) {
        String sql = """
            SELECT 
                COALESCE(s.device_type, 'UNKNOWN') as device_type,
                NULL as browser,
                NULL as os,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW()))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY device_type
            ORDER BY sessions DESC
            """;

        return executeDeviceQuery(sql, startDate, endDate);
    }

    private List<DeviceMetricDto> getByBrowser(Instant startDate, Instant endDate) {
        String sql = """
            SELECT 
                NULL as device_type,
                COALESCE(s.browser, 'Unknown') as browser,
                NULL as os,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW()))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY browser
            ORDER BY sessions DESC
            """;

        return executeDeviceQuery(sql, startDate, endDate);
    }

    private List<DeviceMetricDto> getByOs(Instant startDate, Instant endDate) {
        String sql = """
            SELECT 
                NULL as device_type,
                NULL as browser,
                COALESCE(s.os, 'Unknown') as os,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW()))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY os
            ORDER BY sessions DESC
            """;

        return executeDeviceQuery(sql, startDate, endDate);
    }

    private List<DeviceMetricDto> getByDeviceAndBrowser(Instant startDate, Instant endDate) {
        String sql = """
            SELECT 
                COALESCE(s.device_type, 'UNKNOWN') as device_type,
                COALESCE(s.browser, 'Unknown') as browser,
                NULL as os,
                COUNT(DISTINCT s.session_id) as sessions,
                COUNT(DISTINCT s.visitor_id) as visitors,
                COUNT(DISTINCT CASE WHEN e.event_type = 'PAGE_VIEW' THEN e.event_id END) as page_views,
                COUNT(DISTINCT e.event_id) as total_events,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW()))) as avg_duration,
                (COUNT(DISTINCT CASE WHEN event_count.total = 1 THEN s.session_id END) * 100.0 / COUNT(DISTINCT s.session_id)) as bounce_rate
            FROM sessions s
            LEFT JOIN events e ON s.session_id = e.session_id
            LEFT JOIN (
                SELECT session_id, COUNT(*) as total
                FROM events
                GROUP BY session_id
            ) event_count ON s.session_id = event_count.session_id
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY device_type, browser
            ORDER BY sessions DESC
            LIMIT 20
            """;

        return executeDeviceQuery(sql, startDate, endDate);
    }

    @SuppressWarnings("unchecked")
    private List<DeviceMetricDto> executeDeviceQuery(String sql, Instant startDate, Instant endDate) {
        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter(1, java.sql.Timestamp.from(startDate))
                .setParameter(2, java.sql.Timestamp.from(endDate))
                .getResultList();

        int totalSessions = getTotalSessions(startDate, endDate);

        List<DeviceMetricDto> devices = new ArrayList<>();
        for (Object[] row : results) {
            int sessions = row[3] != null ? ((Number) row[3]).intValue() : 0;
            double percentage = totalSessions > 0
                    ? (sessions * 100.0 / totalSessions)
                    : 0.0;

            devices.add(DeviceMetricDto.builder()
                    .deviceType((String) row[0])
                    .browser((String) row[1])
                    .os((String) row[2])
                    .sessions(sessions)
                    .visitors(row[4] != null ? ((Number) row[4]).intValue() : 0)
                    .pageViews(row[5] != null ? ((Number) row[5]).intValue() : 0)
                    .events(row[6] != null ? ((Number) row[6]).intValue() : 0)
                    .avgSessionDuration(row[7] != null ? ((Number) row[7]).doubleValue() : 0.0)
                    .bounceRate(row[8] != null ? ((Number) row[8]).doubleValue() : 0.0)
                    .percentage(percentage)
                    .build());
        }

        return devices;
    }

    private int getTotalSessions(Instant startDate, Instant endDate) {
        String sql = "SELECT COUNT(DISTINCT session_id) FROM sessions WHERE started_at BETWEEN ?1 AND ?2";

        Number result = (Number) em.createNativeQuery(sql)
                .setParameter(1, java.sql.Timestamp.from(startDate))
                .setParameter(2, java.sql.Timestamp.from(endDate))
                .getSingleResult();

        return result != null ? result.intValue() : 0;
    }

    private DeviceComparisonDto getComparison(Instant startDate, Instant endDate) {
        int totalSessions = getTotalSessions(startDate, endDate);

        DeviceDistributionDto distribution = getDistribution(startDate, endDate, totalSessions);
        DevicePerformanceDto performance = getPerformance(startDate, endDate);

        return DeviceComparisonDto.builder()
                .totalSessions(totalSessions)
                .distribution(distribution)
                .performance(performance)
                .build();
    }

    private DeviceDistributionDto getDistribution(Instant startDate, Instant endDate, int totalSessions) {
        String sql = """
            SELECT 
                device_type,
                COUNT(DISTINCT session_id) as sessions
            FROM sessions
            WHERE started_at BETWEEN ?1 AND ?2
            GROUP BY device_type
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter(1, java.sql.Timestamp.from(startDate))
                .setParameter(2, java.sql.Timestamp.from(endDate))
                .getResultList();

        double mobile = 0, tablet = 0, desktop = 0, bot = 0;

        for (Object[] row : results) {
            String deviceType = (String) row[0];
            int sessions = ((Number) row[1]).intValue();
            double percentage = totalSessions > 0 ? (sessions * 100.0 / totalSessions) : 0.0;

            if (deviceType != null) {
                switch (deviceType.toUpperCase()) {
                    case "MOBILE" -> mobile = percentage;
                    case "TABLET" -> tablet = percentage;
                    case "DESKTOP" -> desktop = percentage;
                    case "BOT" -> bot = percentage;
                }
            }
        }

        return DeviceDistributionDto.builder()
                .mobilePercentage(mobile)
                .tabletPercentage(tablet)
                .desktopPercentage(desktop)
                .botPercentage(bot)
                .build();
    }

    private DevicePerformanceDto getPerformance(Instant startDate, Instant endDate) {
        String sql = """
            SELECT 
                device_type,
                browser,
                os,
                COUNT(DISTINCT s.session_id) as sessions,
                AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW()))) as avg_duration
            FROM sessions s
            WHERE s.started_at BETWEEN ?1 AND ?2
            GROUP BY device_type, browser, os
            ORDER BY sessions DESC
            LIMIT 1
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery(sql)
                .setParameter(1, java.sql.Timestamp.from(startDate))
                .setParameter(2, java.sql.Timestamp.from(endDate))
                .getResultList();

        String topDevice = "Unknown";
        String topBrowser = "Unknown";
        String topOs = "Unknown";

        if (!results.isEmpty()) {
            Object[] row = results.get(0);
            topDevice = row[0] != null ? (String) row[0] : "Unknown";
            topBrowser = row[1] != null ? (String) row[1] : "Unknown";
            topOs = row[2] != null ? (String) row[2] : "Unknown";
        }

        Double avgMobile = getAvgDurationByDevice(startDate, endDate, "MOBILE");
        Double avgDesktop = getAvgDurationByDevice(startDate, endDate, "DESKTOP");

        return DevicePerformanceDto.builder()
                .topDevice(topDevice)
                .topBrowser(topBrowser)
                .topOs(topOs)
                .avgMobileDuration(avgMobile)
                .avgDesktopDuration(avgDesktop)
                .build();
    }

    private Double getAvgDurationByDevice(Instant startDate, Instant endDate, String deviceType) {
        String sql = """
            SELECT AVG(TIMESTAMPDIFF(SECOND, s.started_at, COALESCE(s.ended_at, s.last_activity_at, NOW())))
            FROM sessions s
            WHERE s.started_at BETWEEN ?1 AND ?2
                AND s.device_type = ?3
            """;

        Number result = (Number) em.createNativeQuery(sql)
                .setParameter(1, java.sql.Timestamp.from(startDate))
                .setParameter(2, java.sql.Timestamp.from(endDate))
                .setParameter(3, deviceType)
                .getSingleResult();

        return result != null ? result.doubleValue() : 0.0;
    }
}