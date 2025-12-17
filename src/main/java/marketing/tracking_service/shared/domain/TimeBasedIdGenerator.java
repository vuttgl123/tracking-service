package marketing.tracking_service.shared.domain;

public interface TimeBasedIdGenerator extends IdGenerator {
    long extractTimestamp(String id);

    default boolean isBefore(String id, long timestamp) {
        return extractTimestamp(id) < timestamp;
    }
}
