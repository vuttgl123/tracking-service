package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {

    @Query("select e.eventId from EventEntity e where e.sessionId = :sid and e.clientEventId = :cid")
    Optional<Long> findIdBySessionIdAndClientEventId(@Param("sid") String sessionId,
                                                     @Param("cid") String clientEventId);
}
