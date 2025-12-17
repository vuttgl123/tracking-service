package marketing.tracking_service.shared.domain;

interface TypedIdGenerator<T> {
    T generate();

    T from(String value);
}
