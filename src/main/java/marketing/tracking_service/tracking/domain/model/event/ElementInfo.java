package marketing.tracking_service.tracking.domain.model.event;

public record ElementInfo(String elementId, String elementClass, String elementText) {
    public boolean hasId() {
        return elementId != null && !elementId.isBlank();
    }
}
