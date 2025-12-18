package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.domain.model.attribution.TouchType;
import marketing.tracking_service.tracking.domain.model.attribution.UtmAttribution;
import marketing.tracking_service.tracking.domain.model.attribution.UtmAttributionRepository;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.TouchTypeEnum;
import marketing.tracking_service.tracking.infrastructure.persistence.mapper.UtmAttributionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UtmAttributionRepositoryAdapter implements UtmAttributionRepository {
    private final UtmAttributionJpaRepository jpaRepository;
    private final UtmAttributionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<UtmAttribution> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UtmAttribution> findBySessionAndTouchType(SessionId sessionId, TouchType touchType) {
        return jpaRepository.findBySessionIdAndTouchType(sessionId.value(), toEntityTouchType(touchType)).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UtmAttribution> findByVisitorAndTouchType(VisitorId visitorId, TouchType touchType) {
        return jpaRepository.findByVisitorIdAndTouchType(visitorId.value(), toEntityTouchType(touchType)).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public UtmAttribution save(UtmAttribution attribution) {
        var entity = mapper.toEntity(attribution);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteBySession(SessionId sessionId) {
        jpaRepository.deleteBySessionId(sessionId.value());
    }

    @Override
    @Transactional
    public void deleteByVisitor(VisitorId visitorId) {
        jpaRepository.deleteByVisitorId(visitorId.value());
    }

    private TouchTypeEnum toEntityTouchType(TouchType touchType) {
        return switch (touchType) {
            case FIRST -> TouchTypeEnum.FIRST;
            case LAST  -> TouchTypeEnum.LAST;
        };
    }
}