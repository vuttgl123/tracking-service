package marketing.tracking_service.tracking.domain.model.event;

import lombok.Builder;
import marketing.tracking_service.shared.domain.ValueObject;

import java.util.Objects;

@Builder
public record PageContext(
        String pageUrl,
        String pageTitle,
        String referrerUrl
) implements ValueObject {

    public PageContext {
        Objects.requireNonNull(pageUrl, "Page URL cannot be null");
    }

    public static PageContext of(String pageUrl, String pageTitle, String referrerUrl) {
        return PageContext.builder()
                .pageUrl(pageUrl)
                .pageTitle(pageTitle)
                .referrerUrl(referrerUrl)
                .build();
    }

    public boolean hasReferrer() {
        return referrerUrl != null && !referrerUrl.isBlank();
    }
}