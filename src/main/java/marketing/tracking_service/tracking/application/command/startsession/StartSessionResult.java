package marketing.tracking_service.tracking.application.command.startsession;

import marketing.tracking_service.shared.application.Result;

import java.time.Instant;

record StartSessionResult(
        String visitorId,
        String sessionId,
        Instant startedAt
) implements Result {
}
