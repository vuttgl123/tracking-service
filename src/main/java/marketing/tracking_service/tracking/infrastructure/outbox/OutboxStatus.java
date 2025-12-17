package marketing.tracking_service.tracking.infrastructure.outbox;

public enum OutboxStatus {
    NEW,
    SENT,
    FAILED
}