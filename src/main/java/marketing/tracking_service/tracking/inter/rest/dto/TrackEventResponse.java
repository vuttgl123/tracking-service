package marketing.tracking_service.tracking.inter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TrackEventResponse(
        @JsonProperty("visitor_id")
        String visitorId,

        @JsonProperty("session_id")
        String sessionId,

        @JsonProperty("event_id")
        Long eventId,

        @JsonProperty("client_event_id")
        String clientEventId,

        @JsonProperty("is_duplicate")
        boolean isDuplicate,

        @JsonProperty("status")
        String status
) {

    public static TrackEventResponse success(String visitorId, String sessionId, Long eventId, String clientEventId, boolean isDuplicate) {
        return TrackEventResponse.builder()
                .visitorId(visitorId)
                .sessionId(sessionId)
                .eventId(eventId)
                .clientEventId(clientEventId)
                .isDuplicate(isDuplicate)
                .status("tracked")
                .build();
    }
}