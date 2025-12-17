package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.SessionEntity;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.VisitorEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class SessionWriteService {
    private final SessionJpaRepository sessionJpa;
    private final VisitorJpaRepository visitorJpa;
    private final Clock clock;

    public SessionWriteService(SessionJpaRepository sessionJpa, VisitorJpaRepository visitorJpa, Clock clock) {
        this.sessionJpa = sessionJpa;
        this.visitorJpa = visitorJpa;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureVisitorAndSession(String visitorId, String sessionId,
                                        String ipHash, String userAgent,
                                        String referrerUrl, String landingUrl) {

        visitorJpa.findById(visitorId).orElseGet(() -> {
            var v = new VisitorEntity();
            v.visitorId = visitorId;
            v.firstSeenAt = clock.instant();
            v.lastSeenAt = v.firstSeenAt;
            v.userAgent = userAgent;
            return visitorJpa.save(v);
        });

        sessionJpa.findById(sessionId).orElseGet(() -> {
            var s = new SessionEntity();
            s.sessionId = sessionId;
            s.visitorId = visitorId;
            s.startedAt = clock.instant();
            s.ipHash = ipHash;
            s.userAgent = userAgent;
            s.referrerUrl = referrerUrl;
            s.landingUrl = landingUrl;
            return sessionJpa.save(s);
        });

        sessionJpa.flush();
        visitorJpa.flush();
    }
}

