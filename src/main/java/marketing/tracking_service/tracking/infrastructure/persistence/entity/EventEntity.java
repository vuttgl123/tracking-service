package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import marketing.tracking_service.tracking.infrastructure.persistence.converter.EventTypeEnumConverter;

import java.time.Instant;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "session_id", length = 26, nullable = false)
    private String sessionId;

    @Column(name = "visitor_id", length = 26, nullable = false)
    private String visitorId;

    @Column(name = "client_event_id", length = 36, nullable = false)
    private String clientEventId;

    @Column(name = "event_type", length = 30, nullable = false)
    @Convert(converter = EventTypeEnumConverter.class)
    private EventTypeEnum eventType;

    @Column(name = "event_at", nullable = false)
    private Instant eventAt;

    @Column(name = "page_url", length = 500)
    private String pageUrl;

    @Column(name = "page_title", length = 255)
    private String pageTitle;

    @Column(name = "referrer_url", length = 500)
    private String referrerUrl;

    @Column(name = "scroll_depth")
    private Integer scrollDepth;

    @Column(name = "time_on_page")
    private Integer timeOnPage;

    @Column(name = "viewport_width")
    private Integer viewportWidth;

    @Column(name = "viewport_height")
    private Integer viewportHeight;

    @Column(name = "element_id", length = 100)
    private String elementId;

    @Column(name = "element_class", length = 200)
    private String elementClass;

    @Column(name = "element_text", length = 500)
    private String elementText;

    @Column(name = "meta", columnDefinition = "json")
    private String metaJson;
}

