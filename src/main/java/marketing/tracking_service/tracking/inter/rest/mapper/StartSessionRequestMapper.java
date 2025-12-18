package marketing.tracking_service.tracking.inter.rest.mapper;

import marketing.tracking_service.tracking.application.command.startsession.StartSessionCommand;
import marketing.tracking_service.tracking.inter.rest.dto.StartSessionRequest;
import org.springframework.stereotype.Component;

@Component
public class StartSessionRequestMapper {
    public StartSessionCommand toCommand(StartSessionRequest request, String resolvedIpAddress, String resolvedUserAgent) {
        return StartSessionCommand.builder()
                .visitorId(request.visitorId())
                .sessionId(request.sessionId())
                .ipAddress(request.ipAddress() != null ? request.ipAddress() : resolvedIpAddress)
                .userAgent(request.userAgent() != null ? request.userAgent() : resolvedUserAgent)
                .screenWidth(request.screenWidth())
                .screenHeight(request.screenHeight())
                .language(request.language())
                .timezone(request.timezone())
                .referrerUrl(request.referrerUrl())
                .landingUrl(request.landingUrl())
                .build();
    }
}
