package marketing.tracking_service.shared.domain;

interface Auditable<ID> {
    ID getCreatedBy();

    ID getModifiedBy();

    void setModifiedBy(ID userId);
}
