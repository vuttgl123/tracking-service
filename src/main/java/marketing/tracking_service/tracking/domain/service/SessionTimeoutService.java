package marketing.tracking_service.tracking.domain.service;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.domain.model.session.Session;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.session.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionTimeoutService {

    private final SessionRepository sessionRepository;

    @Value("${tracking.session.timeout-minutes:30}")
    private long timeoutMinutes;

    public Duration getTimeout() {
        return Duration.ofMinutes(timeoutMinutes);
    }

    public boolean isExpired(SessionId sessionId) {
        return sessionRepository.findById(sessionId)
                .map(session -> session.isExpired(getTimeout()))
                .orElse(true);
    }

    public boolean isActive(SessionId sessionId) {
        return !isExpired(sessionId);
    }

    public List<Session> findExpiredSessions() {
        return sessionRepository.findExpiredSessions(getTimeout());
    }

    public void endExpiredSession(SessionId sessionId) {
        sessionRepository.findById(sessionId)
                .filter(Session::isActive)
                .filter(session -> session.isExpired(getTimeout()))
                .ifPresent(session -> {
                    Instant endTime = session.getLastActivityAt().plus(getTimeout());
                    session.end(endTime);
                    sessionRepository.save(session);
                });
    }

    public void endAllExpiredSessions() {
        List<Session> expiredSessions = findExpiredSessions();
        expiredSessions.forEach(session -> {
            Instant endTime = session.getLastActivityAt().plus(getTimeout());
            session.end(endTime);
            sessionRepository.save(session);
        });
    }
}