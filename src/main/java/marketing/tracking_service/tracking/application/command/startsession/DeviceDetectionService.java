package marketing.tracking_service.tracking.application.command.startsession;

import marketing.tracking_service.tracking.domain.model.session.DeviceInfo;

public interface DeviceDetectionService {
    DeviceInfo detect(String userAgent, Integer screenWidth, Integer screenHeight, String language, String timezone);
}
