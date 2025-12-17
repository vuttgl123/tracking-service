package marketing.tracking_service.tracking.infrastructure.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxJpaRepository outboxRepository;
    private final Clock clock;

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String aggregateType, String aggregateId, String eventType, String payload) {
        OutboxMessageEntity message = new OutboxMessageEntity();
        message.aggregateType = aggregateType;
        message.aggregateId = aggregateId;
        message.eventType = eventType;
        message.payloadJson = payload;
        message.status = OutboxStatus.NEW;
        message.retryCount = 0;
        message.nextAttemptAt = clock.instant();
        message.createdAt = clock.instant();

        outboxRepository.save(message);

        log.debug("Outbox message enqueued: type={}, id={}", eventType, aggregateId);
    }

    @Transactional(readOnly = true)
    public List<OutboxMessageEntity> findPendingMessages(int limit) {
        Instant now = clock.instant();
        return outboxRepository.findPendingMessages(now, limit);
    }

    @Transactional
    public void markAsSent(Long outboxId) {
        outboxRepository.findById(outboxId).ifPresent(message -> {
            message.status = OutboxStatus.SENT;
            message.sentAt = clock.instant();
            outboxRepository.save(message);

            log.debug("Outbox message marked as sent: id={}", outboxId);
        });
    }

    @Transactional
    public void markAsFailed(Long outboxId, String error) {
        outboxRepository.findById(outboxId).ifPresent(message -> {
            message.status = OutboxStatus.FAILED;
            message.retryCount++;
            message.lastError = error != null && error.length() > 500
                    ? error.substring(0, 500)
                    : error;
            message.nextAttemptAt = calculateNextAttempt(message.retryCount);

            if (message.retryCount < 5) {
                message.status = OutboxStatus.NEW;
            }

            outboxRepository.save(message);

            log.warn("Outbox message marked as failed: id={}, retryCount={}",
                    outboxId, message.retryCount);
        });
    }

    private Instant calculateNextAttempt(int retryCount) {
        long delaySeconds = (long) Math.pow(2, retryCount) * 60;
        return clock.instant().plusSeconds(delaySeconds);
    }

    @Transactional
    public void deleteOldMessages(int daysOld) {
        Instant cutoff = clock.instant().minusSeconds(daysOld * 86400L);
        int deleted = outboxRepository.deleteOldSentMessages(cutoff);

        if (deleted > 0) {
            log.info("Deleted {} old outbox messages", deleted);
        }
    }
}