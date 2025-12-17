package marketing.tracking_service.tracking.inter.rest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import marketing.tracking_service.tracking.application.command.trackevent.TrackEventCommand;
import marketing.tracking_service.tracking.application.command.trackevent.TrackEventHandler;
import marketing.tracking_service.tracking.domain.model.attribution.UtmData;
import marketing.tracking_service.tracking.inter.rest.dto.TrackEventRequest;
import marketing.tracking_service.tracking.inter.rest.dto.TrackEventResponse;
import marketing.tracking_service.tracking.inter.rest.mapper.TrackEventRequestMapper;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static marketing.tracking_service.tracking.inter.rest.CookieNames.SESSION_ID;
import static marketing.tracking_service.tracking.inter.rest.CookieNames.VISITOR_ID;

@RestController
@RequestMapping("/v1/track")
public class TrackingController {
    private final TrackEventHandler handler;
    private final ObjectMapper objectMapper;
    private final TrackEventRequestMapper mapper;

    public TrackingController(TrackEventHandler handler, ObjectMapper objectMapper, TrackEventRequestMapper mapper) {
        this.handler = handler;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
    }

    @PostMapping("/events")
    public ResponseEntity<TrackEventResponse> track(@RequestBody TrackEventRequest req, HttpServletRequest httpReq) throws Exception {
        String traceId = MDC.get("traceId");

        String cookieVid = getCookie(httpReq, VISITOR_ID);
        String cookieSid = getCookie(httpReq, SESSION_ID);

        String visitorId = firstNonBlank(req.visitorId(), cookieVid);
        String sessionId = firstNonBlank(req.sessionId(), cookieSid);
        String clientEventId = firstNonBlank(req.clientEventId(), UUID.randomUUID().toString());

        String metaJson = (req.meta() == null) ? null : objectMapper.writeValueAsString(req.meta());

        UtmData utm = mapper.toUtm(req.utm());
        TrackEventCommand cmd = mapper.toCommand(traceId, visitorId, sessionId, clientEventId, metaJson, utm, req);

        var result = handler.handle(cmd);

        ResponseCookie vidCookie = ResponseCookie.from(VISITOR_ID, result.visitorId()).path("/").httpOnly(false)
                .secure(false).sameSite("Lax").maxAge(60L * 60 * 24 * 365).build();
        ResponseCookie sidCookie = ResponseCookie.from(SESSION_ID, result.sessionId()).path("/").httpOnly(false)
                .secure(false).sameSite("Lax").maxAge(60L * 60 * 6).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, vidCookie.toString())
                .header(HttpHeaders.SET_COOKIE, sidCookie.toString())
                .body(new TrackEventResponse(result.traceId(), result.visitorId(), result.sessionId(), result.eventId()));
    }

    private static String getCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a.trim();
        if (b != null && !b.isBlank()) return b.trim();
        return null;
    }
}

