package marketing.tracking_service.tracking.domain.model.session;

import marketing.tracking_service.shared.domain.ValueObject;

import java.util.Objects;

public record SessionId(String value) implements ValueObject {

    private static final int ULID_LENGTH = 26;

    public SessionId {
        Objects.requireNonNull(value, "Session ID cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Session ID cannot be blank");
        }
        if (value.length() != ULID_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Invalid session ID format. Expected length: %d, got: %d",
                            ULID_LENGTH, value.length())
            );
        }
    }

    public static SessionId from(String value) {
        return new SessionId(value);
    }

    public static SessionId fromOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new SessionId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}