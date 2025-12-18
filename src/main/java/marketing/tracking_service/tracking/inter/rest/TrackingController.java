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
import marketing.tracking_service.tracking.inter.rest.mapper.StartSessionRequestMapper;
import marketing.tracking_service.tracking.inter.rest.mapper.StartSessionResponseMapper;
import marketing.tracking_service.tracking.inter.rest.mapper.TrackEventRequestMapper;
import marketing.tracking_service.tracking.inter.rest.mapper.TrackEventResponseMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {
    private final TrackEventHandler trackEventHandler;
    private final StartSessionHandler startSessionHandler;
    private final TrackEventRequestMapper trackEventRequestMapper;
    private final TrackEventResponseMapper trackEventResponseMapper;
    private final StartSessionRequestMapper startSessionRequestMapper;
    private final StartSessionResponseMapper startSessionResponseMapper;

    @PostMapping("/events")
    public ResponseEntity<TrackEventResponse> trackEvent(@Valid @RequestBody TrackEventRequest request, HttpServletRequest httpRequest) {
        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = extractUserAgent(httpRequest);

        TrackEventCommand command = trackEventRequestMapper.toCommand(request, ipAddress, userAgent);
        TrackEventResult result = trackEventHandler.handle(command);

        var api = trackEventResponseMapper.toApiResponse(result);
        return ResponseEntity.status(api.status()).body(api.body());
    }

    @PostMapping("/sessions/start")
    public ResponseEntity<StartSessionResponse> startSession(@RequestBody StartSessionRequest request, HttpServletRequest httpRequest){
        String ipAddress = extractIpAddress(httpRequest);
        String userAgent = extractUserAgent(httpRequest);

        StartSessionCommand command = startSessionRequestMapper.toCommand(request, ipAddress, userAgent);
        StartSessionResult result = startSessionHandler.handle(command);

        var api = startSessionResponseMapper.toApiResponse(result);
        return ResponseEntity.status(api.status()).body(api.body());
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

