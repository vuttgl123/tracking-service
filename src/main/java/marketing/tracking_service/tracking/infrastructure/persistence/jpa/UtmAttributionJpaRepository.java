package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.TouchTypeEnum;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.UtmAttributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtmAttributionJpaRepository extends JpaRepository<UtmAttributionEntity, Long> {
    Optional<UtmAttributionEntity> findBySessionIdAndTouchType(String sessionId, TouchTypeEnum touchType);
    Optional<UtmAttributionEntity> findByVisitorIdAndTouchType(String visitorId, TouchTypeEnum touchType);
    void deleteBySessionId(String sessionId);
    void deleteByVisitorId(String visitorId);
}