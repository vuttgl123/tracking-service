package marketing.tracking_service.tracking.application.query.sessions;

import lombok.RequiredArgsConstructor;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSessionDetailHandler {

    private final SessionDetailQueryService queryService;

    @Transactional(readOnly = true)
    public SessionDetailResult handle(GetSessionDetailQuery query) {
        SessionId sessionId = SessionId.from(query.sessionId());
        return queryService.getSessionDetail(sessionId);
    }
}
