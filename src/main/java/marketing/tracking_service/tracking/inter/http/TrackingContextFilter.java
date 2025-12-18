package marketing.tracking_service.tracking.inter.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import marketing.tracking_service.shared.domain.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class TrackingContextFilter extends OncePerRequestFilter {

    private final IdGenerator idGenerator;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/api/v1/tracking");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        try {
            String vid = readCookie(req, "trk_vid");
            String sid = readCookie(req, "trk_sid");

            boolean newVisitor = false;
            boolean newSession = false;

            if (vid == null || vid.isBlank()) {
                vid = idGenerator.newId();
                newVisitor = true;
            }
            if (sid == null || sid.isBlank()) {
                sid = null;
            }

            TrackingContextHolder.set(TrackingContext.builder()
                    .visitorId(vid)
                    .sessionId(sid)
                    .newVisitor(newVisitor)
                    .newSession(false)
                    .build());

            chain.doFilter(req, res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TrackingContextHolder.clear();
        }
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
