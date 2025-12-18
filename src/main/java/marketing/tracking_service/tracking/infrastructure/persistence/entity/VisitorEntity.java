package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "visitors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorEntity {
    @Id
    @Column(name = "visitor_id", length = 26, nullable = false)
    private String visitorId;

    @Column(name = "first_seen_at", nullable = false)
    private Instant firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions;

    @Column(name = "total_page_views", nullable = false)
    private Integer totalPageViews;

    @Column(name = "total_events", nullable = false)
    private Integer totalEvents;

    @Column(name = "first_referrer", length = 500)
    private String firstReferrer;

    @Column(name = "first_landing_page", length = 500)
    private String firstLandingPage;

    @Column(name = "user_agent", length = 400)
    private String userAgent;
}