package marketing.tracking_service.tracking.domain.service;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.shared.domain.IdGenerator;
import marketing.tracking_service.tracking.domain.model.session.*;
import marketing.tracking_service.tracking.domain.model.visitor.Visitor;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionLifecycleService {

    private final SessionRepository sessionRepository;
    private final VisitorRepository visitorRepository;
    private final SessionTimeoutService timeoutService;
    private final IdGenerator idGenerator;
    private final Clock clock;

    public Session startNewSession(
            VisitorId visitorId,
            IpHash ipHash,
            String userAgent,
            DeviceInfo deviceInfo,
            String referrerUrl,
            String landingUrl
    ) {
        SessionId sessionId = SessionId.from(idGenerator.newId());
        Instant now = clock.instant();

        Session session = Session.start(
                sessionId,
                visitorId,
                now,
                ipHash,
                userAgent,
                deviceInfo,
                referrerUrl,
                landingUrl
        );

        return sessionRepository.save(session);
    }

    public Session getOrCreateSession(
            SessionId currentSessionId,
            VisitorId visitorId,
            IpHash ipHash,
            String userAgent,
            DeviceInfo deviceInfo,
            String referrerUrl,
            String landingUrl
    ) {
        Optional<Session> existingSession = sessionRepository.findById(currentSessionId);

        if (existingSession.isPresent() && timeoutService.isActive(currentSessionId)) {
            Session session = existingSession.get();
            session.recordActivity(clock.instant());
            return sessionRepository.save(session);
        }

        if (existingSession.isPresent() && existingSession.get().isActive()) {
            timeoutService.endExpiredSession(currentSessionId);
        }

        visitorRepository.findById(visitorId)
                .ifPresent(Visitor::incrementSessions);

        return startNewSession(visitorId, ipHash, userAgent, deviceInfo, referrerUrl, landingUrl);
    }

    public void endSession(SessionId sessionId) {
        sessionRepository.findById(sessionId)
                .filter(Session::isActive)
                .ifPresent(session -> {
                    session.end(clock.instant());
                    sessionRepository.save(session);
                });
    }

    public void recordActivity(SessionId sessionId) {
        sessionRepository.findById(sessionId)
                .filter(Session::isActive)
                .ifPresent(session -> {
                    session.recordActivity(clock.instant());
                    sessionRepository.save(session);
                });
    }
}