package marketing.tracking_service.shared.domain;

import java.io.Serializable;
public interface ValueObject extends Serializable {
    default void validate() {}
    default boolean isImmutable() {
        return true;
    }
}

