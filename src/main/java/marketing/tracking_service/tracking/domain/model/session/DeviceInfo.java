package marketing.tracking_service.tracking.domain.model.session;

import lombok.Builder;
import marketing.tracking_service.shared.domain.ValueObject;

@Builder
public record DeviceInfo(
        DeviceType deviceType,
        String browser,
        String browserVersion,
        String os,
        String osVersion,
        ScreenResolution screenResolution,
        String language,
        String timezone
) implements ValueObject {

    public DeviceInfo {
        if (deviceType == null) {
            deviceType = DeviceType.UNKNOWN;
        }
    }

    public static DeviceInfo unknown() {
        return DeviceInfo.builder()
                .deviceType(DeviceType.UNKNOWN)
                .build();
    }

    public boolean isMobile() {
        return deviceType == DeviceType.MOBILE;
    }

    public boolean isTablet() {
        return deviceType == DeviceType.TABLET;
    }

    public boolean isDesktop() {
        return deviceType == DeviceType.DESKTOP;
    }

    public boolean isBot() {
        return deviceType == DeviceType.BOT;
    }
}

