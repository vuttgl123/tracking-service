package marketing.tracking_service.tracking.domain.event;

public class DuplicateEventException extends RuntimeException {
    private final String sessionId;
    private final String clientEventId;

    public DuplicateEventException(String sessionId, String clientEventId) {
        super(String.format("Duplicate event detected: sessionId=%s, clientEventId=%s", sessionId, clientEventId));
        this.sessionId = sessionId;
        this.clientEventId = clientEventId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientEventId() {
        return clientEventId;
    }
}