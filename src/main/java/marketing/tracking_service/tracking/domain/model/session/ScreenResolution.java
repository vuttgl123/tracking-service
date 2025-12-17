package marketing.tracking_service.tracking.domain.model.session;

import marketing.tracking_service.shared.domain.ValueObject;

public record ScreenResolution(
        Integer width,
        Integer height
) implements ValueObject {

    public ScreenResolution {
        if (width != null && width <= 0) {
            throw new IllegalArgumentException("Screen width must be positive");
        }
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Screen height must be positive");
        }
    }

    public static ScreenResolution of(Integer width, Integer height) {
        return new ScreenResolution(width, height);
    }

    public boolean isValid() {
        return width != null && height != null;
    }

    @Override
    public String toString() {
        return isValid() ? width + "x" + height : "unknown";
    }
}