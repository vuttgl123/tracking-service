package marketing.tracking_service.tracking.domain.model.attribution;

import lombok.Builder;
import marketing.tracking_service.shared.domain.ValueObject;

@Builder
public record UtmData(
        String utmSource,
        String utmMedium,
        String utmCampaign,
        String utmContent,
        String utmTerm,
        ClickIds clickIds,
        String referrerDomain,
        String landingPage
) implements ValueObject {

    public boolean hasUtmParameters() {
        return utmSource != null || utmMedium != null || utmCampaign != null;
    }

    public boolean hasClickId() {
        return clickIds != null && clickIds.hasAnyId();
    }

    public static UtmData empty() {
        return UtmData.builder().build();
    }
}

