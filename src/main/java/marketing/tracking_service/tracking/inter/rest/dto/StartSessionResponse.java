package marketing.tracking_service.tracking.inter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record StartSessionResponse(
        @JsonProperty("visitor_id")
        String visitorId,

        @JsonProperty("session_id")
        String sessionId,

        @JsonProperty("started_at")
        Instant startedAt,

        @JsonProperty("status")
        String status
) {

    public static StartSessionResponse success(String visitorId, String sessionId, Instant startedAt) {
        return StartSessionResponse.builder()
                .visitorId(visitorId)
                .sessionId(sessionId)
                .startedAt(startedAt)
                .status("started")
                .build();
    }
}