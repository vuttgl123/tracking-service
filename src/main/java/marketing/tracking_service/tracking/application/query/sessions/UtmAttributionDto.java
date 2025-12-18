package marketing.tracking_service.tracking.application.query.sessions;

import lombok.Builder;

@Builder
record UtmAttributionDto(
        String source,
        String medium,
        String campaign,
        String content,
        String term
) {
}
