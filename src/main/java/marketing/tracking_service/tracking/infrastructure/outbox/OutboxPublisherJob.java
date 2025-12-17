package marketing.tracking_service.tracking.infrastructure.outbox;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisherJob {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherJob.class);

    private final OutboxJpaRepository repo;
    private final OutboxPublisher publisher;
    private final OutboxService outboxService;

    public OutboxPublisherJob(OutboxJpaRepository repo, OutboxPublisher publisher, OutboxService outboxService) {
        this.repo = repo;
        this.publisher = publisher;
        this.outboxService = outboxService;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-ms:1000}")
    @Transactional
    public void runOnce() {
        Instant now = outboxService.now();

        List<OutboxMessageEntity> batch = repo.lockBatchDue(now, PageRequest.of(0, 50));
        if (batch.isEmpty()) return;

        for (OutboxMessageEntity msg : batch) {
            try {
                publisher.publish(msg.eventType, msg.payloadJson);

                msg.status = OutboxStatus.SENT;
                msg.sentAt = now;
                msg.lastError = null;

            } catch (Exception ex) {
                msg.status = OutboxStatus.FAILED;
                msg.retryCount += 1;
                msg.lastError = cut(ex.getMessage(), 500);

                long delaySec = Math.min(300, (long) Math.pow(2, Math.min(10, msg.retryCount)));
                msg.nextAttemptAt = now.plusSeconds(delaySec);

                log.warn("outbox_publish_failed outboxId={} retryCount={} nextAttemptAt={} err={}",
                        msg.outboxId, msg.retryCount, msg.nextAttemptAt, msg.lastError);
            }
        }
    }

    private static String cut(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
