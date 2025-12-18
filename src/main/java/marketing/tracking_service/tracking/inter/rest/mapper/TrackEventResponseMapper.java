package marketing.tracking_service.tracking.inter.rest.mapper;

import marketing.tracking_service.tracking.application.command.trackevent.TrackEventResult;
import marketing.tracking_service.tracking.inter.rest.ApiResponse;
import marketing.tracking_service.tracking.inter.rest.dto.TrackEventResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TrackEventResponseMapper {
    public ApiResponse<TrackEventResponse> toApiResponse(TrackEventResult result) {
        TrackEventResponse body = TrackEventResponse.success(
                result.visitorId(),
                result.sessionId(),
                result.eventId(),
                result.clientEventId(),
                result.isDuplicate()
        );

        HttpStatus status = result.isDuplicate() ? HttpStatus.OK : HttpStatus.CREATED;
        return new ApiResponse<>(status, body);
    }
}
