package marketing.tracking_service.tracking.inter.rest.mapper;

import marketing.tracking_service.tracking.application.command.trackevent.TrackEventCommand;
import marketing.tracking_service.tracking.domain.model.attribution.UtmData;
import marketing.tracking_service.tracking.inter.rest.dto.TrackEventRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TrackEventRequestMapper {

    public TrackEventCommand toCommand(
            String traceId,
            String visitorId,
            String sessionId,
            String clientEventId,
            String metaJson,
            UtmData utm,
            TrackEventRequest req
    ) {
        return new TrackEventCommand(
                traceId,
                visitorId,
                sessionId,
                req.eventType(),
                clientEventId,
                (Instant) null,
                req.ipHash(),
                req.userAgent(),
                req.pageUrl(),
                req.pageTitle(),
                req.referrerUrl(),
                req.landingUrl(),
                metaJson,
                utm
        );
    }

    public UtmData toUtm(TrackEventRequest.UtmDto u) {
        if (u == null) return null;
        return new UtmData(u.source(), u.medium(), u.campaign(), u.term(), u.content(), u.clickId());
    }
}
