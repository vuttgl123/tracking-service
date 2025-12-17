package marketing.tracking_service.tracking.infrastructure.persistence.entity;


import jakarta.persistence.*;
import marketing.tracking_service.tracking.infrastructure.outbox.OutboxStatus;

import java.time.Instant;

@Entity
@Table(name = "outbox_messages")
public class OutboxMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    public Long outboxId;

    @Column(name = "aggregate_type", length = 50, nullable = false)
    public String aggregateType;

    @Column(name = "aggregate_id", length = 50, nullable = false)
    public String aggregateId;

    @Column(name = "event_type", length = 50, nullable = false)
    public String eventType;

    @Column(name = "payload", columnDefinition = "json", nullable = false)
    public String payloadJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    public OutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    public int retryCount;

    @Column(name = "next_attempt_at", nullable = false)
    public Instant nextAttemptAt;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "sent_at")
    public Instant sentAt;

    @Column(name = "last_error", length = 500)
    public String lastError;
}
