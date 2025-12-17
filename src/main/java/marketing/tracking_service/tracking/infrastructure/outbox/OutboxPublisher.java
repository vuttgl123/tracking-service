package marketing.tracking_service.tracking.infrastructure.outbox;

public interface OutboxPublisher {
    void publish(String eventType, String payloadJson) throws Exception;
}
