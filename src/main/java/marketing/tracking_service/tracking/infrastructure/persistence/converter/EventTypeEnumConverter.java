package marketing.tracking_service.tracking.infrastructure.persistence.converter;

import jakarta.persistence.Converter;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.EventTypeEnum;


@Converter
public class EventTypeEnumConverter extends LowercaseEnumConverter<EventTypeEnum> {
    public EventTypeEnumConverter() {
        super(EventTypeEnum.class);
    }
}
