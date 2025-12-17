package marketing.tracking_service.tracking.domain.model.attribution;

import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;

import java.util.Optional;

public interface UtmAttributionRepository {

    Optional<UtmAttribution> findById(Long id);

    Optional<UtmAttribution> findBySessionAndTouchType(SessionId sessionId, TouchType touchType);

    Optional<UtmAttribution> findByVisitorAndTouchType(VisitorId visitorId, TouchType touchType);

    UtmAttribution save(UtmAttribution attribution);

    void deleteBySession(SessionId sessionId);

    void deleteByVisitor(VisitorId visitorId);
}