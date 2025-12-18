package marketing.tracking_service.tracking.application.command.trackevent;

import lombok.Builder;

@Builder
public record UtmParameters(
        String source,
        String medium,
        String campaign,
        String content,
        String term,
        String clickId,
        String gclid,
        String fbclid,
        String referrerDomain
) {
    public boolean hasAnyParameter() {
        return source != null || medium != null || campaign != null || clickId != null || gclid != null || fbclid != null;
    }
}
