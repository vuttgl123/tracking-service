package marketing.tracking_service.tracking.inter.rest;

import marketing.tracking_service.tracking.application.query.sessions.SessionDetailQueryService;
import marketing.tracking_service.tracking.inter.rest.dto.SessionDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sessions")
public class SessionQueryController {

    private final SessionDetailQueryService service;

    public SessionQueryController(SessionDetailQueryService service) {
        this.service = service;
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDetailResponse> get(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "50") int limit
    ) {
        if (limit <= 0) limit = 50;
        if (limit > 200) limit = 200;

        return ResponseEntity.ok(service.getBySessionId(sessionId, limit));
    }
}
