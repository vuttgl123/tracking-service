package marketing.tracking_service.tracking.domain.model.session;

import marketing.tracking_service.shared.domain.ValueObject;

import java.util.Objects;

public record SessionId(String value) implements ValueObject {

    private static final int ULID_LENGTH = 26;

    public SessionId {
        Objects.requireNonNull(value, "Session ID cannot be null");
        if (value.isBlank() || value.length() != ULID_LENGTH) {
            throw new IllegalArgumentException("Invalid session ID format");
        }
    }

    public static SessionId from(String value) {
        return new SessionId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}