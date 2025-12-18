package marketing.tracking_service.tracking.inter.rest.mapper;

import marketing.tracking_service.tracking.application.command.startsession.StartSessionResult;
import marketing.tracking_service.tracking.inter.rest.ApiResponse;
import marketing.tracking_service.tracking.inter.rest.dto.StartSessionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class StartSessionResponseMapper {
    public ApiResponse<StartSessionResponse> toApiResponse(StartSessionResult result) {
        StartSessionResponse body = StartSessionResponse.success(result.visitorId(), result.sessionId(), result.startedAt());
        return new ApiResponse<>(HttpStatus.CREATED, body);
    }
}
