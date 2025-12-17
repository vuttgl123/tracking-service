package marketing.tracking_service.tracking.domain.model.visitor;

import java.util.Optional;

public interface VisitorRepository {

    Optional<Visitor> findById(VisitorId id);

    boolean existsById(VisitorId id);

    Visitor save(Visitor visitor);

    void delete(VisitorId id);
}