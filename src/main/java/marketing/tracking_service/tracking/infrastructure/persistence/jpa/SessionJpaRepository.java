package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SessionJpaRepository extends JpaRepository<SessionEntity, String> {
    List<SessionEntity> findByVisitorIdAndEndedAtIsNull(String visitorId);
    Optional<SessionEntity> findFirstByVisitorIdOrderByStartedAtDesc(String visitorId);
    @Query("""
        SELECT s FROM SessionEntity s 
        WHERE s.endedAt IS NULL 
        AND s.lastActivityAt < :cutoff
        """)
    List<SessionEntity> findExpiredSessions(@Param("cutoff") Instant cutoff);
}