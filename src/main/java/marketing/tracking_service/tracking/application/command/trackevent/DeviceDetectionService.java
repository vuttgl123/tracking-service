package marketing.tracking_service.tracking.application.command.trackevent;

import marketing.tracking_service.tracking.domain.model.session.DeviceInfo;

interface DeviceDetectionService {
    DeviceInfo detect(String userAgent, Integer screenWidth, Integer screenHeight,
                      String language, String timezone);
}
