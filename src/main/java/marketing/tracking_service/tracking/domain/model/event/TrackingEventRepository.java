package marketing.tracking_service.tracking.domain.model.event;

import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TrackingEventRepository {
    Optional<TrackingEvent> findById(Long id);
    Optional<TrackingEvent> findBySessionAndClientEventId(SessionId sessionId, String clientEventId);
    boolean existsBySessionAndClientEventId(SessionId sessionId, String clientEventId);
    TrackingEvent save(TrackingEvent event);
    List<TrackingEvent> findBySession(SessionId sessionId);
    List<TrackingEvent> findByVisitor(VisitorId visitorId, Instant from, Instant to);
    long countBySession(SessionId sessionId);
    long countPageViewsBySession(SessionId sessionId);
}