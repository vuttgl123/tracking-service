package marketing.tracking_service.tracking.application.command.startsession;

import lombok.Builder;
import marketing.tracking_service.shared.application.Command;

@Builder
public record StartSessionCommand(
        String visitorId,
        String sessionId,
        String ipAddress,
        String userAgent,
        Integer screenWidth,
        Integer screenHeight,
        String language,
        String timezone,
        String referrerUrl,
        String landingUrl
) implements Command {
}