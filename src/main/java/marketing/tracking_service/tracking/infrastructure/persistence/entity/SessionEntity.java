package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import marketing.tracking_service.tracking.infrastructure.persistence.converter.DeviceTypeEnumConverter;

import java.time.Instant;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionEntity {
    @Id
    @Column(name = "session_id", length = 26, nullable = false)
    private String sessionId;

    @Column(name = "visitor_id", length = 26, nullable = false)
    private String visitorId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @Column(name = "ip_hash", length = 64)
    private String ipHash;

    @Column(name = "user_agent", length = 400)
    private String userAgent;

    @Column(name = "device_type", length = 20)
    @Convert(converter = DeviceTypeEnumConverter.class)
    private DeviceTypeEnum deviceType;

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "browser_version", length = 20)
    private String browserVersion;

    @Column(name = "os", length = 50)
    private String os;

    @Column(name = "os_version", length = 20)
    private String osVersion;

    @Column(name = "screen_width")
    private Integer screenWidth;

    @Column(name = "screen_height")
    private Integer screenHeight;

    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "referrer_url", length = 500)
    private String referrerUrl;

    @Column(name = "landing_url", length = 500)
    private String landingUrl;
}

