package marketing.tracking_service.tracking.inter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record TrackEventRequest(

        @JsonProperty("visitor_id")
        String visitorId,

        @JsonProperty("session_id")
        String sessionId,

        @JsonProperty("client_event_id")
        @NotBlank(message = "client_event_id is required for idempotency")
        String clientEventId,

        @JsonProperty("event_type")
        @NotNull(message = "event_type is required")
        String eventType,

        @JsonProperty("event_at")
        Instant eventAt,

        @JsonProperty("page_url")
        String pageUrl,

        @JsonProperty("page_title")
        String pageTitle,

        @JsonProperty("referrer_url")
        String referrerUrl,

        @JsonProperty("landing_url")
        String landingUrl,

        @JsonProperty("scroll_depth")
        Integer scrollDepth,

        @JsonProperty("time_on_page")
        Integer timeOnPage,

        @JsonProperty("viewport_width")
        Integer viewportWidth,

        @JsonProperty("viewport_height")
        Integer viewportHeight,

        @JsonProperty("screen_width")
        Integer screenWidth,

        @JsonProperty("screen_height")
        Integer screenHeight,

        @JsonProperty("element_id")
        String elementId,

        @JsonProperty("element_class")
        String elementClass,

        @JsonProperty("element_text")
        String elementText,

        @JsonProperty("language")
        String language,

        @JsonProperty("timezone")
        String timezone,

        @JsonProperty("user_agent")
        String userAgent,

        @JsonProperty("ip_address")
        String ipAddress,

        @JsonProperty("metadata")
        Map<String, Object> metadata,

        @JsonProperty("utm")
        UtmDto utm
) {
}

