package marketing.tracking_service.tracking.domain.model.session;

import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findById(SessionId id);

    boolean existsById(SessionId id);

    Session save(Session session);

    void delete(SessionId id);

    List<Session> findActiveSessionsByVisitor(VisitorId visitorId);

    List<Session> findExpiredSessions(Duration timeout);

    Optional<Session> findLatestByVisitor(VisitorId visitorId);
}