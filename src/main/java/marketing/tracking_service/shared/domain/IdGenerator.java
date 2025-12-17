package marketing.tracking_service.shared.domain;

public interface IdGenerator {
    String newId();
    default String newId(String prefix) {
        return prefix + "_" + newId();
    }

    default boolean isValid(String id) {
        return id != null && !id.isBlank();
    }

    default String getStrategy() {
        return this.getClass().getSimpleName();
    }
}

