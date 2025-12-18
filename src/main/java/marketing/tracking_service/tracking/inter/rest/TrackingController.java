package marketing.tracking_service.tracking.inter.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.tracking.application.command.startsession.StartSessionCommand;
import marketing.tracking_service.tracking.application.command.startsession.StartSessionHandler;
import marketing.tracking_service.tracking.application.command.startsession.StartSessionResult;
import marketing.tracking_service.tracking.application.command.trackevent.TrackEventCommand;
import marketing.tracking_service.tracking.application.command.trackevent.TrackEventHandler;
import marketing.tracking_service.tracking.application.command.trackevent.TrackEventResult;
import marketing.tracking_service.tracking.inter.rest.dto.*;
import marketing.tracking_service.tracking.inter.rest.mapper.TrackEventRequestMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackEventHandler trackEventHandler;
    private final StartSessionHandler startSessionHandler;
    private final TrackEventRequestMapper requestMapper;

    @PostMapping("/events")
    public ResponseEntity<TrackEventResponse> trackEvent(
            @Valid @RequestBody TrackEventRequest request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = extractUserAgent(httpRequest);

        TrackEventCommand command = requestMapper.toCommand(request, ipAddress, userAgent);
        TrackEventResult result = trackEventHandler.handle(command);

        TrackEventResponse response = TrackEventResponse.success(
                result.visitorId(),
                result.sessionId(),
                result.eventId(),
                result.clientEventId(),
                result.isDuplicate()
        );

        HttpStatus status = result.isDuplicate() ? HttpStatus.OK : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/sessions/start")
    public ResponseEntity<StartSessionResponse> startSession(
            @RequestBody StartSessionRequest request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = request.ipAddress() != null
                ? request.ipAddress()
                : extractIpAddress(httpRequest);
        String userAgent = request.userAgent() != null
                ? request.userAgent()
                : extractUserAgent(httpRequest);

        StartSessionCommand command = StartSessionCommand.builder()
                .visitorId(request.visitorId())
                .sessionId(request.sessionId())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .screenWidth(request.screenWidth())
                .screenHeight(request.screenHeight())
                .language(request.language())
                .timezone(request.timezone())
                .referrerUrl(request.referrerUrl())
                .landingUrl(request.landingUrl())
                .build();

        StartSessionResult result = startSessionHandler.handle(command);

        StartSessionResponse response = StartSessionResponse.success(
                result.visitorId(),
                result.sessionId(),
                result.startedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("ok", "Tracking service is running"));
    }

    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String extractUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}

