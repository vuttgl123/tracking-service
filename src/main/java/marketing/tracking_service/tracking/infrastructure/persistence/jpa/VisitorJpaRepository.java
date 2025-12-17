package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.VisitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorJpaRepository extends JpaRepository<VisitorEntity, String> {}