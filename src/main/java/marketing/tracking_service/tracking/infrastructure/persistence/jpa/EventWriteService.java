package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import marketing.tracking_service.tracking.domain.event.DuplicateEventException;
import marketing.tracking_service.tracking.domain.model.event.TrackingEventType;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.EventEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EventWriteService {

    private final EventJpaRepository jpa;

    public EventWriteService(EventJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long insertEventOrThrowDuplicate(
            String sessionId,
            String visitorId,
            String clientEventId,
            TrackingEventType type,
            Instant eventAt,
            String pageUrl,
            String pageTitle,
            String referrerUrl,
            String metaJson
    ) {
        try {
            EventEntity e = new EventEntity();
            e.sessionId = sessionId;
            e.visitorId = visitorId;
            e.clientEventId = clientEventId;
            e.eventType = type.name();
            e.eventAt = eventAt;
            e.pageUrl = pageUrl;
            e.pageTitle = pageTitle;
            e.referrerUrl = referrerUrl;
            e.metaJson = metaJson;

            return jpa.saveAndFlush(e).eventId;

        } catch (DataIntegrityViolationException ex) {
            if (isDuplicateKey(ex)) throw new DuplicateEventException();
            throw ex;
        }
    }

    private static boolean isDuplicateKey(DataIntegrityViolationException ex) {
        Throwable t = ex;
        while (t != null) {
            String msg = t.getMessage();
            if (msg != null && msg.contains("Duplicate entry") && msg.contains("uk_session_client_event")) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
