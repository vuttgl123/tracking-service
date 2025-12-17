package marketing.tracking_service.tracking.application.command.captureutm;

import marketing.tracking_service.shared.application.Result;

record CaptureUtmResult(
        boolean firstTouchCaptured,
        boolean lastTouchCaptured
) implements Result {
}
