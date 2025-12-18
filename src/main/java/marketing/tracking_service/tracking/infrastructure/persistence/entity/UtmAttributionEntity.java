package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "utm_attributions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtmAttributionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "utm_id")
    private Long utmId;

    @Column(name = "session_id", length = 26)
    private String sessionId;

    @Column(name = "visitor_id", length = 26)
    private String visitorId;

    @Column(name = "utm_source", length = 100)
    private String utmSource;

    @Column(name = "utm_medium", length = 100)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 150)
    private String utmCampaign;

    @Column(name = "utm_content", length = 150)
    private String utmContent;

    @Column(name = "utm_term", length = 150)
    private String utmTerm;

    @Column(name = "click_id", length = 200)
    private String clickId;

    @Column(name = "gclid", length = 200)
    private String gclid;

    @Column(name = "fbclid", length = 200)
    private String fbclid;

    @Column(name = "referrer_domain", length = 255)
    private String referrerDomain;

    @Column(name = "landing_page", length = 500)
    private String landingPage;

    @Column(name = "touch_type", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private TouchTypeEnum touchType;

    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;
}

