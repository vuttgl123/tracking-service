package marketing.tracking_service.tracking.domain.model.attribution;

import lombok.Builder;

@Builder
public record ClickIds(
        String genericClickId,
        String gclid,
        String fbclid
) {
    public boolean hasAnyId() {
        return genericClickId != null || gclid != null || fbclid != null;
    }

    public boolean isGoogleAds() {
        return gclid != null;
    }

    public boolean isFacebookAds() {
        return fbclid != null;
    }
}
