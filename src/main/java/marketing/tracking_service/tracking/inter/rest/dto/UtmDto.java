package marketing.tracking_service.tracking.inter.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UtmDto(
        @JsonProperty("source")
        String source,

        @JsonProperty("medium")
        String medium,

        @JsonProperty("campaign")
        String campaign,

        @JsonProperty("content")
        String content,

        @JsonProperty("term")
        String term,

        @JsonProperty("click_id")
        String clickId,

        @JsonProperty("gclid")
        String gclid,

        @JsonProperty("fbclid")
        String fbclid,

        @JsonProperty("referrer_domain")
        String referrerDomain
) {
}
