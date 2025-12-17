package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "sessions")
public class SessionEntity {
    @Id
    @Column(name = "session_id", length = 26, nullable = false)
    public String sessionId;

    @Column(name = "visitor_id", length = 26, nullable = false)
    public String visitorId;

    @Column(name = "started_at", nullable = false)
    public Instant startedAt;

    @Column(name = "ended_at")
    public Instant endedAt;

    @Column(name = "ip_hash", length = 64)
    public String ipHash;

    @Column(name = "user_agent", length = 400)
    public String userAgent;

    @Column(name = "referrer_url", length = 500)
    public String referrerUrl;

    @Column(name = "landing_url", length = 500)
    public String landingUrl;
}