package marketing.tracking_service.tracking.inter.http;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class TrackingContext {
    String visitorId;
    String sessionId;
    boolean newVisitor;
    boolean newSession;
}