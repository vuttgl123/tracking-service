package marketing.tracking_service.tracking.domain.model.event;

public enum TrackingEventType {
    PAGE_VIEW,
    VIEW_CONTENT,
    SCROLL,
    CLICK,
    FORM_START,
    FORM_SUBMIT,
    LEAD_SUBMIT,
    SIGNUP,
    DOWNLOAD,
    VIDEO_PLAY,
    VIDEO_COMPLETE,
    CUSTOM;

    public boolean isPageView() {
        return this == PAGE_VIEW;
    }

    public boolean isInteraction() {
        return this == CLICK || this == SCROLL;
    }

    public boolean isConversion() {
        return this == LEAD_SUBMIT || this == SIGNUP;
    }
}