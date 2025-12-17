package marketing.tracking_service.tracking.domain.model.attribution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import marketing.tracking_service.shared.domain.AggregateRoot;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;

import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UtmAttribution extends AggregateRoot<Long> {

    private SessionId sessionId;
    private VisitorId visitorId;
    private UtmData utmData;
    private TouchType touchType;
    private Instant capturedAt;

    private UtmAttribution(
            SessionId sessionId,
            VisitorId visitorId,
            UtmData utmData,
            TouchType touchType,
            Instant capturedAt
    ) {
        super();
        this.sessionId = sessionId;
        this.visitorId = visitorId;
        this.utmData = Objects.requireNonNull(utmData);
        this.touchType = Objects.requireNonNull(touchType);
        this.capturedAt = Objects.requireNonNull(capturedAt);
    }

    public static UtmAttribution capture(
            SessionId sessionId,
            VisitorId visitorId,
            UtmData utmData,
            TouchType touchType,
            Instant capturedAt
    ) {
        return new UtmAttribution(sessionId, visitorId, utmData, touchType, capturedAt);
    }

    public static UtmAttribution captureFirstTouch(
            SessionId sessionId,
            VisitorId visitorId,
            UtmData utmData,
            Instant capturedAt
    ) {
        return capture(sessionId, visitorId, utmData, TouchType.FIRST, capturedAt);
    }

    public static UtmAttribution captureLastTouch(
            SessionId sessionId,
            VisitorId visitorId,
            UtmData utmData,
            Instant capturedAt
    ) {
        return capture(sessionId, visitorId, utmData, TouchType.LAST, capturedAt);
    }

    public void updateUtmData(UtmData newUtmData) {
        this.utmData = Objects.requireNonNull(newUtmData);
        applyStateChange();
    }

    public boolean isFirstTouch() {
        return touchType.isFirst();
    }

    public boolean isLastTouch() {
        return touchType.isLast();
    }

    public boolean hasUtmParameters() {
        return utmData.hasUtmParameters();
    }

    @Override
    public void validate() {
        if (utmData == null) {
            throw new IllegalStateException("UTM data is required");
        }
        if (touchType == null) {
            throw new IllegalStateException("Touch type is required");
        }
        if (capturedAt == null) {
            throw new IllegalStateException("Captured at is required");
        }
    }
}