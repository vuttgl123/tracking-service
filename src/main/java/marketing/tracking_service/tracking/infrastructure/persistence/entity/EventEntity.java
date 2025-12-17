package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    public Long eventId;

    @Column(name = "session_id", length = 26, nullable = false)
    public String sessionId;

    @Column(name = "visitor_id", length = 26, nullable = false)
    public String visitorId;

    @Column(name = "event_type", length = 30, nullable = false)
    public String eventType;

    @Column(name = "event_at", nullable = false)
    public Instant eventAt;

    @Column(name = "page_url", length = 500)
    public String pageUrl;

    @Column(name = "page_title", length = 255)
    public String pageTitle;

    @Column(name = "referrer_url", length = 500)
    public String referrerUrl;

    @Column(name = "meta", columnDefinition = "json")
    public String metaJson;

    @Column(name = "client_event_id", length = 36, nullable = false)
    public String clientEventId;
}
