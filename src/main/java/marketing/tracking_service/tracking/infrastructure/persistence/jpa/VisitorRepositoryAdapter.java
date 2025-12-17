package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.domain.model.visitor.Visitor;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorRepository;
import marketing.tracking_service.tracking.infrastructure.persistence.mapper.VisitorMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VisitorRepositoryAdapter implements VisitorRepository {

    private final VisitorJpaRepository jpaRepository;
    private final VisitorMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Visitor> findById(VisitorId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(VisitorId id) {
        return jpaRepository.existsById(id.value());
    }

    @Override
    @Transactional
    public Visitor save(Visitor visitor) {
        var entity = mapper.toEntity(visitor);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(VisitorId id) {
        jpaRepository.deleteById(id.value());
    }
}