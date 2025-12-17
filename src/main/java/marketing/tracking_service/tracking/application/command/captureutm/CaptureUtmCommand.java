package marketing.tracking_service.tracking.application.command.captureutm;

import lombok.Builder;
import marketing.tracking_service.shared.application.Command;

@Builder
record CaptureUtmCommand(
        String visitorId,
        String sessionId,
        String utmSource,
        String utmMedium,
        String utmCampaign,
        String utmContent,
        String utmTerm,
        String clickId,
        String gclid,
        String fbclid,
        String referrerDomain,
        String landingPage
) implements Command {}

