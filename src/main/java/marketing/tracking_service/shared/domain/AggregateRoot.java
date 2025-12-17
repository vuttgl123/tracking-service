package marketing.tracking_service.shared.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class AggregateRoot<ID> extends Entity<ID> {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private Long version = 0L;

    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(ID id) {
        super(id);
    }

    protected void registerEvent(DomainEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Domain event cannot be null");
        }
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    public boolean hasDomainEvents() {
        return !this.domainEvents.isEmpty();
    }

    protected void incrementVersion() {
        this.version++;
        markAsModified();
    }

    protected void applyStateChange() {
        incrementVersion();
    }

    @Override
    public void validate() {
        super.validate();
    }

    public String getAggregateType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, version=%d]",
                getClass().getSimpleName(), getId(), version);
    }
}

