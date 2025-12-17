package marketing.tracking_service.tracking.domain.model.event;

import lombok.Builder;
import marketing.tracking_service.shared.domain.ValueObject;

@Builder
public record InteractionContext(
        Integer scrollDepth,
        Integer timeOnPage,
        ViewportSize viewport,
        ElementInfo element
) implements ValueObject {

    public InteractionContext {
        if (scrollDepth != null && (scrollDepth < 0 || scrollDepth > 100)) {
            throw new IllegalArgumentException("Scroll depth must be between 0 and 100");
        }
        if (timeOnPage != null && timeOnPage < 0) {
            throw new IllegalArgumentException("Time on page cannot be negative");
        }
    }

    public static InteractionContext empty() {
        return InteractionContext.builder().build();
    }

    public boolean hasScrollData() {
        return scrollDepth != null;
    }

    public boolean hasTimeData() {
        return timeOnPage != null;
    }

    public boolean hasElementData() {
        return element != null;
    }
}

