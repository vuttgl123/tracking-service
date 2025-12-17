package marketing.tracking_service.shared.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Getter
public abstract class Entity<ID> implements Serializable {
    @Setter(AccessLevel.PROTECTED)
    private ID id;

    @Setter(AccessLevel.PROTECTED)
    private Instant createdAt;

    @Setter(AccessLevel.PROTECTED)
    private Instant updatedAt;

    protected Entity() {
    }

    protected Entity(ID id) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    protected void markAsModified() {
        this.updatedAt = Instant.now();
    }

    public boolean isPersisted() {
        return this.id != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Entity<?> other = (Entity<?>) obj;

        if (!this.isPersisted() || !other.isPersisted()) {
            return false;
        }

        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return isPersisted() ? Objects.hashCode(id) : 0;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s]", getClass().getSimpleName(), id);
    }

    public void validate() {
        if (id == null) {
            throw new IllegalStateException("Entity ID is required");
        }
    }
}