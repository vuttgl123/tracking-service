package marketing.tracking_service.tracking.domain.model.visitor;

import marketing.tracking_service.shared.domain.ValueObject;

import java.util.Objects;

public record VisitorId(String value) implements ValueObject {

    private static final int ULID_LENGTH = 26;

    public VisitorId {
        Objects.requireNonNull(value, "Visitor ID cannot be null");
        if (value.isBlank() || value.length() != ULID_LENGTH) {
            throw new IllegalArgumentException("Invalid visitor ID format");
        }
    }

    public static VisitorId from(String value) {
        return new VisitorId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}