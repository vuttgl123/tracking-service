package marketing.tracking_service.tracking.infrastructure.persistence.mapper;

import marketing.tracking_service.tracking.domain.model.attribution.*;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;

import marketing.tracking_service.tracking.infrastructure.persistence.entity.TouchTypeEnum;
import marketing.tracking_service.tracking.infrastructure.persistence.entity.UtmAttributionEntity;
import org.springframework.stereotype.Component;

@Component
public class UtmAttributionMapper {

    public UtmAttributionEntity toEntity(UtmAttribution attribution) {
        return UtmAttributionEntity.builder()
                .utmId(attribution.getId())
                .sessionId(attribution.getSessionId() != null ? attribution.getSessionId().value() : null)
                .visitorId(attribution.getVisitorId() != null ? attribution.getVisitorId().value() : null)
                .utmSource(attribution.getUtmData().utmSource())
                .utmMedium(attribution.getUtmData().utmMedium())
                .utmCampaign(attribution.getUtmData().utmCampaign())
                .utmContent(attribution.getUtmData().utmContent())
                .utmTerm(attribution.getUtmData().utmTerm())
                .clickId(attribution.getUtmData().clickIds() != null
                        ? attribution.getUtmData().clickIds().genericClickId() : null)
                .gclid(attribution.getUtmData().clickIds() != null
                        ? attribution.getUtmData().clickIds().gclid() : null)
                .fbclid(attribution.getUtmData().clickIds() != null
                        ? attribution.getUtmData().clickIds().fbclid() : null)
                .referrerDomain(attribution.getUtmData().referrerDomain())
                .landingPage(attribution.getUtmData().landingPage())
                .touchType(TouchTypeEnum.valueOf(attribution.getTouchType().name()))
                .capturedAt(attribution.getCapturedAt())
                .build();
    }

    public UtmAttribution toDomain(UtmAttributionEntity entity) {
        UtmData utmData = UtmData.builder()
                .utmSource(entity.getUtmSource())
                .utmMedium(entity.getUtmMedium())
                .utmCampaign(entity.getUtmCampaign())
                .utmContent(entity.getUtmContent())
                .utmTerm(entity.getUtmTerm())
                .clickIds(ClickIds.builder()
                        .genericClickId(entity.getClickId())
                        .gclid(entity.getGclid())
                        .fbclid(entity.getFbclid())
                        .build())
                .referrerDomain(entity.getReferrerDomain())
                .landingPage(entity.getLandingPage())
                .build();

        UtmAttribution attribution = UtmAttribution.capture(
                entity.getSessionId() != null ? SessionId.from(entity.getSessionId()) : null,
                entity.getVisitorId() != null ? VisitorId.from(entity.getVisitorId()) : null,
                utmData,
                TouchType.valueOf(entity.getTouchType().name()),
                entity.getCapturedAt()
        );

        if (entity.getUtmId() != null) {
            attribution.setId(entity.getUtmId());
        }

        return attribution;
    }
}