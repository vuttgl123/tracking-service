package marketing.tracking_service.tracking.domain.model.visitor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import marketing.tracking_service.shared.domain.AggregateRoot;
import marketing.tracking_service.tracking.domain.event.VisitorFirstSeen;

import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visitor extends AggregateRoot<VisitorId> {

    private Instant firstSeenAt;
    private Instant lastSeenAt;
    private int totalSessions;
    private int totalPageViews;
    private int totalEvents;
    private String firstReferrer;
    private String firstLandingPage;
    private String userAgent;

    private Visitor(
            VisitorId id,
            Instant firstSeenAt,
            String firstReferrer,
            String firstLandingPage,
            String userAgent
    ) {
        super(id);
        this.firstSeenAt = Objects.requireNonNull(firstSeenAt);
        this.lastSeenAt = firstSeenAt;
        this.totalSessions = 1;
        this.totalPageViews = 0;
        this.totalEvents = 0;
        this.firstReferrer = firstReferrer;
        this.firstLandingPage = firstLandingPage;
        this.userAgent = userAgent;

        registerEvent(VisitorFirstSeen.of(
                id.value(),
                firstSeenAt,
                firstReferrer,
                firstLandingPage,
                userAgent
        ));
    }

    public static Visitor create(
            VisitorId id,
            Instant firstSeenAt,
            String firstReferrer,
            String firstLandingPage,
            String userAgent
    ) {
        return new Visitor(id, firstSeenAt, firstReferrer, firstLandingPage, userAgent);
    }

    public void markAsSeen() {
        this.lastSeenAt = Instant.now();
        applyStateChange();
    }

    public void incrementSessions() {
        this.totalSessions++;
        markAsSeen();
    }

    public void incrementPageViews() {
        this.totalPageViews++;
        applyStateChange();
    }

    public void incrementEvents(int count) {
        this.totalEvents += count;
        applyStateChange();
    }

    public boolean isNew() {
        return totalSessions == 1;
    }

    public boolean isReturning() {
        return totalSessions > 1;
    }

    public boolean isLoyal() {
        return totalSessions >= 5;
    }

    @Override
    public void validate() {
        super.validate();
        if (firstSeenAt == null) {
            throw new IllegalStateException("First seen at is required");
        }
        if (lastSeenAt == null) {
            throw new IllegalStateException("Last seen at is required");
        }
        if (totalSessions < 1) {
            throw new IllegalStateException("Total sessions must be at least 1");
        }
    }
}