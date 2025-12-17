package marketing.tracking_service.tracking.infrastructure.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogOutboxPublisher implements OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(LogOutboxPublisher.class);

    @Override
    public void publish(String eventType, String payloadJson) {
        log.info("OUTBOX_PUBLISH eventType={} payload={}", eventType, payloadJson);
    }
}
