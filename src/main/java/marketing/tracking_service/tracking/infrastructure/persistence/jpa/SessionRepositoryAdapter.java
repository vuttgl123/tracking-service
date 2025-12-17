package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.session.SessionRepository;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.SessionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Repository
public class SessionRepositoryAdapter implements SessionRepository {
    private final SessionJpaRepository jpa;
    private final Clock clock;

    @PersistenceContext
    private EntityManager em;


    public SessionRepositoryAdapter(SessionJpaRepository jpa, Clock clock) {
        this.jpa = jpa;
        this.clock = clock;
    }

    @Override
    public Optional<SessionId> findById(SessionId id) {
        return jpa.findById(id.value()).map(s -> new SessionId(s.sessionId));
    }

    @Transactional
    public void createIfNotExists(SessionId id, VisitorId visitorId,
                                  String ipHash, String userAgent,
                                  String referrerUrl, String landingUrl) {
        em.createNativeQuery("""
    INSERT IGNORE INTO sessions
      (session_id, visitor_id, started_at, ended_at, ip_hash, user_agent, referrer_url, landing_url)
    VALUES
      (?, ?, CURRENT_TIMESTAMP, NULL, ?, ?, ?, ?)
""")
                .setParameter(1, id.value())
                .setParameter(2, visitorId.value())
                .setParameter(3, ipHash)
                .setParameter(4, userAgent)
                .setParameter(5, referrerUrl)
                .setParameter(6, landingUrl)
                .executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> getLastActivityAt(SessionId sessionId) {
        Object v = em.createNativeQuery("""
      SELECT COALESCE(ended_at, started_at) FROM sessions WHERE session_id = ?
    """)
                .setParameter(1, sessionId.value())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (v == null) return Optional.empty();
        if (v instanceof java.sql.Timestamp ts) return Optional.of(ts.toInstant());
        return Optional.empty();
    }

    @Override
    @Transactional
    public void touchActivity(SessionId sessionId, Instant at) {
        em.createNativeQuery("""
      UPDATE sessions SET ended_at = ? WHERE session_id = ?
    """)
                .setParameter(1, java.sql.Timestamp.from(at))
                .setParameter(2, sessionId.value())
                .executeUpdate();
    }

}
