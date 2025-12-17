package marketing.tracking_service.shared.domain;

interface SoftDeletable {
    void markAsDeleted();

    boolean isDeleted();

    void restore();
}
