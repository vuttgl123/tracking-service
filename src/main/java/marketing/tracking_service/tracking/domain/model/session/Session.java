package marketing.tracking_service.tracking.domain.model.session;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import marketing.tracking_service.shared.domain.AggregateRoot;
import marketing.tracking_service.tracking.domain.event.SessionStarted;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session extends AggregateRoot<SessionId> {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(30);

    private VisitorId visitorId;
    private Instant startedAt;
    private Instant endedAt;
    private Instant lastActivityAt;
    private IpHash ipHash;
    private String userAgent;
    private DeviceInfo deviceInfo;
    private String referrerUrl;
    private String landingUrl;

    private Session(SessionId id, VisitorId visitorId, Instant startedAt, IpHash ipHash, String userAgent, DeviceInfo deviceInfo, String referrerUrl, String landingUrl) {
        super(id);
        this.visitorId = Objects.requireNonNull(visitorId);
        this.startedAt = Objects.requireNonNull(startedAt);
        this.lastActivityAt = startedAt;
        this.ipHash = ipHash;
        this.userAgent = userAgent;
        this.deviceInfo = deviceInfo != null ? deviceInfo : DeviceInfo.unknown();
        this.referrerUrl = referrerUrl;
        this.landingUrl = landingUrl;

        registerEvent(SessionStarted.of(id.value(), visitorId.value(), startedAt, ipHash != null ? ipHash.value() : null, userAgent, referrerUrl, landingUrl));
    }

    public static Session start(SessionId id, VisitorId visitorId, Instant startedAt, IpHash ipHash, String userAgent, DeviceInfo deviceInfo, String referrerUrl, String landingUrl) {
        return new Session(id, visitorId, startedAt, ipHash, userAgent, deviceInfo, referrerUrl, landingUrl);
    }

    public static Session reconstruct(SessionId id, VisitorId visitorId, Instant startedAt, Instant endedAt, Instant lastActivityAt, IpHash ipHash, String userAgent, DeviceInfo deviceInfo, String referrerUrl, String landingUrl) {
        Session session = new Session();
        session.setId(id);
        session.visitorId = visitorId;
        session.startedAt = startedAt;
        session.endedAt = endedAt;
        session.lastActivityAt = lastActivityAt;
        session.ipHash = ipHash;
        session.userAgent = userAgent;
        session.deviceInfo = deviceInfo;
        session.referrerUrl = referrerUrl;
        session.landingUrl = landingUrl;
        return session;
    }

    public void recordActivity(Instant activityAt) {
        if (isEnded()) {
            throw new IllegalStateException("Cannot record activity on ended session");
        }

        this.lastActivityAt = Objects.requireNonNull(activityAt);
        applyStateChange();
    }

    public void end(Instant endedAt) {
        if (isEnded()) {
            throw new IllegalStateException("Session already ended");
        }

        this.endedAt = Objects.requireNonNull(endedAt);
        applyStateChange();
    }

    public boolean isActive() {
        return endedAt == null;
    }

    public boolean isEnded() {
        return endedAt != null;
    }

    public boolean isExpired(Duration timeout) {
        if (isEnded()) {
            return true;
        }

        Duration inactivity = Duration.between(lastActivityAt, Instant.now());
        return inactivity.compareTo(timeout) > 0;
    }

    public boolean isExpired() {
        return isExpired(DEFAULT_TIMEOUT);
    }

    public Duration getDuration() {
        Instant end = endedAt != null ? endedAt : Instant.now();
        return Duration.between(startedAt, end);
    }

    public Duration getInactivityDuration() {
        return Duration.between(lastActivityAt, Instant.now());
    }

    @Override
    public void validate() {
        super.validate();
        if (visitorId == null) {
            throw new IllegalStateException("Visitor ID is required");
        }
        if (startedAt == null) {
            throw new IllegalStateException("Started at is required");
        }
        if (lastActivityAt == null) {
            throw new IllegalStateException("Last activity at is required");
        }
        if (endedAt != null && endedAt.isBefore(startedAt)) {
            throw new IllegalStateException("End time cannot be before start time");
        }
    }
}