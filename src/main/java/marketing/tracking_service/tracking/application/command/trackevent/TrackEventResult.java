package marketing.tracking_service.tracking.application.command.trackevent;

import lombok.Builder;
import marketing.tracking_service.shared.application.Result;

@Builder
public record TrackEventResult(
        String visitorId,
        String sessionId,
        Long eventId,
        String clientEventId,
        boolean isDuplicate
) implements Result {

    public static TrackEventResult success(String visitorId, String sessionId, Long eventId, String clientEventId) {
        return new TrackEventResult(visitorId, sessionId, eventId, clientEventId, false);
    }

    public static TrackEventResult duplicate(String visitorId, String sessionId, Long eventId, String clientEventId) {
        return new TrackEventResult(visitorId, sessionId, eventId, clientEventId, true);
    }
}