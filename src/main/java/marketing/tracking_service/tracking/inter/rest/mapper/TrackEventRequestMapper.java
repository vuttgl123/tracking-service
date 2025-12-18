package marketing.tracking_service.tracking.inter.rest.mapper;

import marketing.tracking_service.tracking.application.command.trackevent.TrackEventCommand;
import marketing.tracking_service.tracking.application.command.trackevent.UtmParameters;
import marketing.tracking_service.tracking.domain.model.event.TrackingEventType;
import marketing.tracking_service.tracking.inter.http.TrackingContextHolder;
import marketing.tracking_service.tracking.inter.rest.dto.TrackEventRequest;
import marketing.tracking_service.tracking.inter.rest.dto.UtmDto;
import org.springframework.stereotype.Component;

@Component
public class TrackEventRequestMapper {

    public TrackEventCommand toCommand(TrackEventRequest request, String resolvedIpAddress, String resolvedUserAgent) {
        var ctx = TrackingContextHolder.get();
        return TrackEventCommand.builder()
                .visitorId(ctx != null ? ctx.getVisitorId() : request.visitorId())
                .sessionId(ctx != null ? ctx.getSessionId() : request.sessionId())
                .clientEventId(request.clientEventId())
                .eventType(parseEventType(request.eventType()))
                .eventAt(request.eventAt())
                .ipAddress(request.ipAddress() != null ? request.ipAddress() : resolvedIpAddress)
                .userAgent(request.userAgent() != null ? request.userAgent() : resolvedUserAgent)
                .screenWidth(request.screenWidth())
                .screenHeight(request.screenHeight())
                .language(request.language())
                .timezone(request.timezone())
                .pageUrl(request.pageUrl())
                .pageTitle(request.pageTitle())
                .referrerUrl(request.referrerUrl())
                .landingUrl(request.landingUrl())
                .scrollDepth(request.scrollDepth())
                .timeOnPage(request.timeOnPage())
                .viewportWidth(request.viewportWidth())
                .viewportHeight(request.viewportHeight())
                .elementId(request.elementId())
                .elementClass(request.elementClass())
                .elementText(request.elementText())
                .metadata(request.metadata())
                .utm(mapUtm(request.utm()))
                .build();
    }

    private TrackingEventType parseEventType(String eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("event_type is required");
        }

        try {
            return TrackingEventType.valueOf(eventType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid event_type: " + eventType);
        }
    }

    private UtmParameters mapUtm(UtmDto utm) {
        if (utm == null) {
            return null;
        }

        return UtmParameters.builder()
                .source(utm.source())
                .medium(utm.medium())
                .campaign(utm.campaign())
                .content(utm.content())
                .term(utm.term())
                .clickId(utm.clickId())
                .gclid(utm.gclid())
                .fbclid(utm.fbclid())
                .referrerDomain(utm.referrerDomain())
                .build();
    }
}