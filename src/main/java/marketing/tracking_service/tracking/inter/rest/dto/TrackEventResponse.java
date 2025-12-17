package marketing.tracking_service.tracking.inter.rest.dto;

public record TrackEventResponse(
        String traceId,
        String visitorId,
        String sessionId,
        long eventId
) {}
