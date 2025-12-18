package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.domain.event.DuplicateEventException;
import marketing.tracking_service.tracking.domain.model.event.TrackingEvent;
import marketing.tracking_service.tracking.domain.model.event.TrackingEventRepository;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.infrastructure.persistence.mapper.TrackingEventMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TrackingEventRepositoryAdapter implements TrackingEventRepository {
    private final EventJpaRepository jpaRepository;
    private final TrackingEventMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<TrackingEvent> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrackingEvent> findBySessionAndClientEventId(SessionId sessionId, String clientEventId) {
        return jpaRepository.findBySessionIdAndClientEventId(sessionId.value(), clientEventId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySessionAndClientEventId(SessionId sessionId, String clientEventId) {
        return jpaRepository.existsBySessionIdAndClientEventId(sessionId.value(), clientEventId);
    }

    @Override
    @Transactional
    public TrackingEvent save(TrackingEvent event) {
        try {
            var entity = mapper.toEntity(event);
            var saved = jpaRepository.saveAndFlush(entity);

            TrackingEvent domainEvent = mapper.toDomain(saved);

            if (!domainEvent.isPersisted()) {
                domainEvent.markAsPersisted(saved.getEventId());
            }

            return domainEvent;

        } catch (DataIntegrityViolationException ex) {
            if (isDuplicateKeyException(ex)) {
                throw new DuplicateEventException(event.getSessionId().value(), event.getClientEventId());
            }
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackingEvent> findBySession(SessionId sessionId) {
        return jpaRepository.findBySessionIdOrderByEventAtDesc(sessionId.value())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackingEvent> findByVisitor(VisitorId visitorId, Instant from, Instant to) {
        return jpaRepository.findByVisitorIdAndEventAtBetween(visitorId.value(), from, to)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countBySession(SessionId sessionId) {
        return jpaRepository.countBySessionId(sessionId.value());
    }

    @Override
    @Transactional(readOnly = true)
    public long countPageViewsBySession(SessionId sessionId) {
        return jpaRepository.countBySessionIdAndEventType(sessionId.value(), "PAGE_VIEW");
    }

    private boolean isDuplicateKeyException(DataIntegrityViolationException ex) {
        Throwable cause = ex;
        while (cause != null) {
            String message = cause.getMessage();
            if (message != null &&
                    message.contains("Duplicate entry") &&
                    message.contains("uk_session_client_event")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}