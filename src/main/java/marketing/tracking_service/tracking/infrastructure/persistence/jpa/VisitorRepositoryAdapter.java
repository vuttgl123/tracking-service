package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorRepository;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.VisitorEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Repository
public class VisitorRepositoryAdapter implements VisitorRepository {
    private final VisitorJpaRepository jpa;
    private final Clock clock;

    @PersistenceContext
    private EntityManager em;

    public VisitorRepositoryAdapter(VisitorJpaRepository jpa, Clock clock) {
        this.jpa = jpa;
        this.clock = clock;
    }

    @Override
    public Optional<VisitorId> findById(VisitorId id) {
        return jpa.findById(id.value()).map(v -> new VisitorId(v.visitorId));
    }

    @Transactional
    public void createIfNotExists(VisitorId id, String userAgent) {
        em.createNativeQuery("""
    INSERT IGNORE INTO visitors (visitor_id, first_seen_at, last_seen_at, user_agent)
    VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)
  """)
                .setParameter(1, id.value())
                .setParameter(2, userAgent)
                .executeUpdate();
    }

    @Override
    public void touchLastSeen(VisitorId id) {
        jpa.findById(id.value()).ifPresent(v -> {
            v.lastSeenAt = clock.instant();
            jpa.save(v);
        });
    }
}
