package marketing.tracking_service.tracking.inter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record StartSessionRequest(
        @JsonProperty("visitor_id")
        String visitorId,

        @JsonProperty("session_id")
        String sessionId,

        @JsonProperty("screen_width")
        Integer screenWidth,

        @JsonProperty("screen_height")
        Integer screenHeight,

        @JsonProperty("language")
        String language,

        @JsonProperty("timezone")
        String timezone,

        @JsonProperty("user_agent")
        String userAgent,

        @JsonProperty("ip_address")
        String ipAddress,

        @JsonProperty("referrer_url")
        String referrerUrl,

        @JsonProperty("landing_url")
        String landingUrl
) {}