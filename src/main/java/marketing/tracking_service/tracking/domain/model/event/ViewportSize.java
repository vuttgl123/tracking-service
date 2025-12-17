package marketing.tracking_service.tracking.domain.model.event;

public record ViewportSize(Integer width, Integer height) {
    public ViewportSize {
        if (width != null && width <= 0) {
            throw new IllegalArgumentException("Viewport width must be positive");
        }
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Viewport height must be positive");
        }
    }
}
