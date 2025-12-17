package marketing.tracking_service.shared.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface DomainEvent extends Serializable {
    Instant occurredAt();
    String eventType();

    default String eventId() {
        return UUID.randomUUID().toString();
    }

    default String version() {
        return "1.0";
    }

    String aggregateId();

    default String aggregateType() {
        return this.getClass().getSimpleName().replace("Event", "");
    }
}

