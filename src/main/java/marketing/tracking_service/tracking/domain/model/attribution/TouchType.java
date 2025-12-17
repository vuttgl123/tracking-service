package marketing.tracking_service.tracking.domain.model.attribution;

public enum TouchType {
    FIRST,
    LAST;

    public boolean isFirst() {
        return this == FIRST;
    }

    public boolean isLast() {
        return this == LAST;
    }
}
