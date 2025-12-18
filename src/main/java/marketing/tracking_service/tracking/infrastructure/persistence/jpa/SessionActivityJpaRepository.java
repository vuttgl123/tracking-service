package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.SessionActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SessionActivityJpaRepository extends JpaRepository<SessionActivityEntity, Long> {

    @Query("SELECT MAX(sa.activityAt) FROM SessionActivityEntity sa WHERE sa.sessionId = :sessionId")
    Optional<Instant> findLastActivityTime(@Param("sessionId") String sessionId);

    long countBySessionId(String sessionId);
}