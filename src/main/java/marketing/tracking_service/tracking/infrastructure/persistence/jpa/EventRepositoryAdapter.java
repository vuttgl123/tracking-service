package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.domain.model.event.TrackingEventRepository;
import marketing.tracking_service.tracking.domain.model.event.TrackingEventType;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.EventEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class EventRepositoryAdapter implements TrackingEventRepository {

    private final EventWriteService writer;
    private final EventJpaRepository jpa;

    public EventRepositoryAdapter(EventWriteService writer, EventJpaRepository jpa) {
        this.writer = writer;
        this.jpa = jpa;
    }

    @Override
    public long insert(SessionId sessionId, VisitorId visitorId, String clientEventId,
                       TrackingEventType type, Instant eventAt,
                       String pageUrl, String pageTitle, String referrerUrl, String metaJson) {
        return writer.insertEventOrThrowDuplicate(
                sessionId.value(),
                visitorId.value(),
                clientEventId,
                type,
                eventAt,
                pageUrl,
                pageTitle,
                referrerUrl,
                metaJson
        );
    }

    @Override
    public Optional<Long> findEventIdBySessionAndClientEvent(SessionId sessionId, String clientEventId) {
        return jpa.findIdBySessionIdAndClientEventId(sessionId.value(), clientEventId);
    }
}