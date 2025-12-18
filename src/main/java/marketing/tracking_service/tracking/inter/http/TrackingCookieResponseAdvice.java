package marketing.tracking_service.tracking.inter.http;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.inter.rest.dto.StartSessionResponse;
import marketing.tracking_service.tracking.inter.rest.dto.TrackEventResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;

@Component
@RestControllerAdvice
@RequiredArgsConstructor
public class TrackingCookieResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(response instanceof ServletServerHttpResponse servletResp)) return body;

        String path = request.getURI().getPath();
        if (path == null || !path.startsWith("/api/v1/tracking")) return body;

        String visitorId = null;
        String sessionId = null;

        if (body instanceof TrackEventResponse r) {
            visitorId = r.visitorId();
            sessionId = r.sessionId();
        } else if (body instanceof StartSessionResponse r) {
            visitorId = r.visitorId();
            sessionId = r.sessionId();
        } else {
            return body;
        }

        HttpServletResponse httpResp = servletResp.getServletResponse();

        // trk_vid: long-lived
        if (visitorId != null) {
            httpResp.addHeader(HttpHeaders.SET_COOKIE,
                    cookie("trk_vid", visitorId, Duration.ofDays(365)).toString());
        }

        // trk_sid: session-ish
        if (sessionId != null) {
            httpResp.addHeader(HttpHeaders.SET_COOKIE,
                    cookie("trk_sid", sessionId, Duration.ofMinutes(30)).toString());
        }

        return body;
    }

    private ResponseCookie cookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(false)
                .secure(false)
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();
    }
}
