package marketing.tracking_service.tracking.infrastructure.persistence.converter;


import jakarta.persistence.Converter;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.DeviceTypeEnum;

@Converter
public class DeviceTypeEnumConverter extends LowercaseEnumConverter<DeviceTypeEnum> {
    public DeviceTypeEnumConverter() {
        super(DeviceTypeEnum.class);
    }
}