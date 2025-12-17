package marketing.tracking_service.tracking.infrastructure.persistence.mapper;

import marketing.tracking_service.tracking.domain.model.session.*;
import marketing.tracking_service.tracking.domain.model.session.DeviceType;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;


import marketing.tracking_service.tracking.infrastructure.persistence.entity.DeviceTypeEnum;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.SessionEntity;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionEntity toEntity(Session session) {
        return SessionEntity.builder()
                .sessionId(session.getId().value())
                .visitorId(session.getVisitorId().value())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .lastActivityAt(session.getLastActivityAt())
                .ipHash(session.getIpHash() != null ? session.getIpHash().value() : null)
                .userAgent(session.getUserAgent())
                .deviceType(mapDeviceType(session.getDeviceInfo()))
                .browser(session.getDeviceInfo().browser())
                .browserVersion(session.getDeviceInfo().browserVersion())
                .os(session.getDeviceInfo().os())
                .osVersion(session.getDeviceInfo().osVersion())
                .screenWidth(session.getDeviceInfo().screenResolution() != null
                        ? session.getDeviceInfo().screenResolution().width() : null)
                .screenHeight(session.getDeviceInfo().screenResolution() != null
                        ? session.getDeviceInfo().screenResolution().height() : null)
                .language(session.getDeviceInfo().language())
                .timezone(session.getDeviceInfo().timezone())
                .referrerUrl(session.getReferrerUrl())
                .landingUrl(session.getLandingUrl())
                .build();
    }

    public Session toDomain(SessionEntity entity) {
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceType(mapDeviceType(entity.getDeviceType()))
                .browser(entity.getBrowser())
                .browserVersion(entity.getBrowserVersion())
                .os(entity.getOs())
                .osVersion(entity.getOsVersion())
                .screenResolution(entity.getScreenWidth() != null && entity.getScreenHeight() != null
                        ? ScreenResolution.of(entity.getScreenWidth(), entity.getScreenHeight())
                        : null)
                .language(entity.getLanguage())
                .timezone(entity.getTimezone())
                .build();

        Session session = Session.start(
                SessionId.from(entity.getSessionId()),
                VisitorId.from(entity.getVisitorId()),
                entity.getStartedAt(),
                entity.getIpHash() != null ? IpHash.from(entity.getIpHash()) : null,
                entity.getUserAgent(),
                deviceInfo,
                entity.getReferrerUrl(),
                entity.getLandingUrl()
        );

        session.clearDomainEvents();

        return session;
    }

    private DeviceTypeEnum mapDeviceType(DeviceInfo deviceInfo) {
        if (deviceInfo == null || deviceInfo.deviceType() == null) {
            return DeviceTypeEnum.UNKNOWN;
        }
        return DeviceTypeEnum.valueOf(deviceInfo.deviceType().name());
    }

    private DeviceType mapDeviceType(DeviceTypeEnum deviceType) {
        if (deviceType == null) {
            return DeviceType.UNKNOWN;
        }
        return DeviceType.valueOf(deviceType.name());
    }
}