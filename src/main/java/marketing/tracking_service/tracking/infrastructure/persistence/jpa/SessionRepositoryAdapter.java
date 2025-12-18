package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.domain.model.session.Session;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.session.SessionRepository;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.infrastructure.persistence.mapper.SessionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryAdapter implements SessionRepository {
    private final SessionJpaRepository jpaRepository;
    private final SessionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Session> findById(SessionId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(SessionId id) {
        return jpaRepository.existsById(id.value());
    }

    @Override
    @Transactional
    public Session save(Session session) {
        var entity = mapper.toEntity(session);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(SessionId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> findActiveSessionsByVisitor(VisitorId visitorId) {
        return jpaRepository.findByVisitorIdAndEndedAtIsNull(visitorId.value())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> findExpiredSessions(Duration timeout) {
        Instant cutoff = Instant.now().minus(timeout);
        return jpaRepository.findExpiredSessions(cutoff)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Session> findLatestByVisitor(VisitorId visitorId) {
        return jpaRepository.findFirstByVisitorIdOrderByStartedAtDesc(visitorId.value()).map(mapper::toDomain);
    }
}