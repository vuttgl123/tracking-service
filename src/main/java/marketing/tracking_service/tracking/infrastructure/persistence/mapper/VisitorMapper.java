package marketing.tracking_service.tracking.infrastructure.persistence.mapper;

import marketing.tracking_service.tracking.domain.model.visitor.Visitor;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.VisitorEntity;
import org.springframework.stereotype.Component;

@Component
public class VisitorMapper {

    public VisitorEntity toEntity(Visitor visitor) {
        return VisitorEntity.builder()
                .visitorId(visitor.getId().value())
                .firstSeenAt(visitor.getFirstSeenAt())
                .lastSeenAt(visitor.getLastSeenAt())
                .totalSessions(visitor.getTotalSessions())
                .totalPageViews(visitor.getTotalPageViews())
                .totalEvents(visitor.getTotalEvents())
                .firstReferrer(visitor.getFirstReferrer())
                .firstLandingPage(visitor.getFirstLandingPage())
                .userAgent(visitor.getUserAgent())
                .build();
    }

    public Visitor toDomain(VisitorEntity entity) {
        Visitor visitor = Visitor.reconstruct(
                VisitorId.from(entity.getVisitorId()),
                entity.getFirstSeenAt(),
                entity.getLastSeenAt(),
                entity.getTotalSessions(),
                entity.getTotalPageViews(),
                entity.getTotalEvents(),
                entity.getFirstReferrer(),
                entity.getFirstLandingPage(),
                entity.getUserAgent()
        );

        visitor.clearDomainEvents();
        return visitor;
    }
}