package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "visitors")
public class VisitorEntity {
    @Id
    @Column(name = "visitor_id", length = 26, nullable = false)
    public String visitorId;

    @Column(name = "first_seen_at", nullable = false)
    public Instant firstSeenAt;

    @Column(name = "last_seen_at", nullable = false)
    public Instant lastSeenAt;

    @Column(name = "user_agent", length = 400)
    public String userAgent;
}
