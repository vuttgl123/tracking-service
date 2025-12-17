package marketing.tracking_service.tracking.infrastructure.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.OutboxMessageEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "tracking.outbox.publisher.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class OutboxPublisherJob {

    private final OutboxService outboxService;
    private final OutboxPublisher outboxPublisher;

    @Scheduled(fixedDelayString = "${tracking.outbox.publisher.interval-ms:5000}")
    public void publishPendingMessages() {
        try {
            List<OutboxMessageEntity> messages = outboxService.findPendingMessages(100);

            if (messages.isEmpty()) {
                return;
            }

            log.debug("Processing {} outbox messages", messages.size());

            for (OutboxMessageEntity message : messages) {
                try {
                    outboxPublisher.publish(message);
                    outboxService.markAsSent(message.outboxId);

                } catch (Exception e) {
                    log.error("Failed to publish outbox message: id={}",
                            message.outboxId, e);
                    outboxService.markAsFailed(message.outboxId, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error in outbox publisher job", e);
        }
    }

    @Scheduled(cron = "${tracking.outbox.cleanup.cron:0 0 2 * * ?}")
    public void cleanupOldMessages() {
        try {
            outboxService.deleteOldMessages(7);
        } catch (Exception e) {
            log.error("Error cleaning up old outbox messages", e);
        }
    }
}