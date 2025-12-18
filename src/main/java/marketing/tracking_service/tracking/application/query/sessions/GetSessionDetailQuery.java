package marketing.tracking_service.tracking.application.query.sessions;

import lombok.Builder;
import marketing.tracking_service.shared.application.Query;

@Builder
public record GetSessionDetailQuery(
        String sessionId
) implements Query {}

