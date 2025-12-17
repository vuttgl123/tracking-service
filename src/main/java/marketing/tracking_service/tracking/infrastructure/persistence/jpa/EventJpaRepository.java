package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {

    Optional<EventEntity> findBySessionIdAndClientEventId(String sessionId, String clientEventId);

    boolean existsBySessionIdAndClientEventId(String sessionId, String clientEventId);

    List<EventEntity> findBySessionIdOrderByEventAtAsc(String sessionId);

    List<EventEntity> findByVisitorIdAndEventAtBetween(String visitorId, Instant from, Instant to);

    long countBySessionId(String sessionId);

    long countBySessionIdAndEventType(String sessionId, String eventType);
}