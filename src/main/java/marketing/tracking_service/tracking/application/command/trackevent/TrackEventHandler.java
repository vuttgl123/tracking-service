package marketing.tracking_service.tracking.application.command.trackevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.shared.application.CommandHandler;
import marketing.tracking_service.shared.domain.DomainEventPublisher;
import marketing.tracking_service.shared.domain.IdGenerator;
import marketing.tracking_service.tracking.application.command.startsession.DeviceDetectionService;
import marketing.tracking_service.tracking.domain.event.DuplicateEventException;
import marketing.tracking_service.tracking.domain.model.attribution.*;
import marketing.tracking_service.tracking.domain.model.event.*;
import marketing.tracking_service.tracking.domain.model.session.*;
import marketing.tracking_service.tracking.domain.model.visitor.Visitor;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorRepository;
import marketing.tracking_service.tracking.domain.service.SessionLifecycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackEventHandler implements CommandHandler<TrackEventCommand, TrackEventResult> {

    private final IdGenerator idGenerator;
    private final Clock clock;
    private final VisitorRepository visitorRepository;
    private final SessionRepository sessionRepository;
    private final TrackingEventRepository eventRepository;
    private final UtmAttributionRepository utmRepository;
    private final SessionLifecycleService sessionLifecycle;
    private final DomainEventPublisher eventPublisher;
    private final DeviceDetectionService deviceDetection;

    @Override
    @Transactional
    public TrackEventResult handle(TrackEventCommand cmd) {
        VisitorId visitorId = resolveVisitorId(cmd.visitorId());
        SessionId sessionId = resolveSessionId(cmd.sessionId());

        Visitor visitor = getOrCreateVisitor(visitorId, cmd);
        Visitor savedVisitor = visitorRepository.save(visitor);

        IpHash ipHash = cmd.ipAddress() != null
                ? IpHash.fromIpAddress(cmd.ipAddress())
                : null;

        DeviceInfo deviceInfo = deviceDetection.detect(
                cmd.userAgent(),
                cmd.screenWidth(),
                cmd.screenHeight(),
                cmd.language(),
                cmd.timezone()
        );

        Session session = sessionLifecycle.getOrCreateSession(
                sessionId,
                visitorId,
                ipHash,
                cmd.userAgent(),
                deviceInfo,
                cmd.referrerUrl(),
                cmd.landingUrl()
        );

        if (cmd.hasUtm()) {
            captureUtmAttribution(session, visitor, cmd);
        }

        TrackingEvent event = createTrackingEvent(session, visitor, cmd);

        try {
            TrackingEvent savedEvent = eventRepository.save(event);

            sessionLifecycle.recordActivity(session.getId());
            visitor.incrementEvents(1);
            if (cmd.eventType().isPageView()) {
                visitor.incrementPageViews();
            }

            visitorRepository.save(visitor);

            eventPublisher.publishFrom(savedEvent);
            eventPublisher.publishFrom(session);
            eventPublisher.publishFrom(visitor);

            log.info("Event tracked: eventId={}, type={}, sessionId={}",
                    savedEvent.getId(), cmd.eventType(), session.getId());

            return TrackEventResult.success(
                    visitor.getId().value(),
                    session.getId().value(),
                    savedEvent.getId(),
                    savedEvent.getClientEventId()
            );

        } catch (DuplicateEventException e) {
            log.warn("Duplicate event detected: sessionId={}, clientEventId={}",
                    session.getId(), cmd.clientEventId());

            return eventRepository.findBySessionAndClientEventId(
                            session.getId(),
                            cmd.clientEventId()
                    )
                    .map(existing -> TrackEventResult.duplicate(
                            visitor.getId().value(),
                            session.getId().value(),
                            existing.getId(),
                            existing.getClientEventId()
                    ))
                    .orElseThrow(() -> new IllegalStateException(
                            "Duplicate event but cannot find existing event"));
        }
    }

    private VisitorId resolveVisitorId(String visitorIdStr) {
        VisitorId visitorId = VisitorId.fromOrNull(visitorIdStr);
        return visitorId != null ? visitorId : VisitorId.from(idGenerator.newId());
    }

    private SessionId resolveSessionId(String sessionIdStr) {
        SessionId sessionId = SessionId.fromOrNull(sessionIdStr);
        return sessionId != null ? sessionId : SessionId.from(idGenerator.newId());
    }

    private Visitor getOrCreateVisitor(VisitorId visitorId, TrackEventCommand cmd) {
        return visitorRepository.findById(visitorId)
                .map(visitor -> {
                    visitor.markAsSeen();
                    return visitor;
                })
                .orElseGet(() -> Visitor.create(
                        visitorId,
                        clock.instant(),
                        cmd.referrerUrl(),
                        cmd.landingUrl(),
                        cmd.userAgent()
                ));
    }

    private TrackingEvent createTrackingEvent(Session session, Visitor visitor, TrackEventCommand cmd) {
        PageContext pageContext = PageContext.builder()
                .pageUrl(cmd.pageUrl())
                .pageTitle(cmd.pageTitle())
                .referrerUrl(cmd.referrerUrl())
                .build();

        InteractionContext interactionContext = InteractionContext.builder()
                .scrollDepth(cmd.scrollDepth())
                .timeOnPage(cmd.timeOnPage())
                .viewport(cmd.viewportWidth() != null && cmd.viewportHeight() != null
                        ? new ViewportSize(cmd.viewportWidth(), cmd.viewportHeight())
                        : null)
                .element(cmd.elementId() != null || cmd.elementClass() != null
                        ? new ElementInfo(cmd.elementId(), cmd.elementClass(), cmd.elementText())
                        : null)
                .build();

        Instant eventAt = cmd.eventAt() != null ? cmd.eventAt() : clock.instant();

        return TrackingEvent.track(
                session.getId(),
                visitor.getId(),
                cmd.clientEventId(),
                cmd.eventType(),
                eventAt,
                pageContext,
                interactionContext,
                cmd.metadata()
        );
    }

    private void captureUtmAttribution(Session session, Visitor visitor, TrackEventCommand cmd) {
        UtmData utmData = UtmData.builder()
                .utmSource(cmd.utm().source())
                .utmMedium(cmd.utm().medium())
                .utmCampaign(cmd.utm().campaign())
                .utmContent(cmd.utm().content())
                .utmTerm(cmd.utm().term())
                .clickIds(ClickIds.builder()
                        .genericClickId(cmd.utm().clickId())
                        .gclid(cmd.utm().gclid())
                        .fbclid(cmd.utm().fbclid())
                        .build())
                .referrerDomain(cmd.utm().referrerDomain())
                .landingPage(cmd.landingUrl())
                .build();

        Instant now = clock.instant();

        if (!utmRepository.findByVisitorAndTouchType(visitor.getId(), TouchType.FIRST).isPresent()) {
            UtmAttribution firstTouch = UtmAttribution.captureFirstTouch(
                    session.getId(),
                    visitor.getId(),
                    utmData,
                    now
            );
            utmRepository.save(firstTouch);
        }

        utmRepository.findBySessionAndTouchType(session.getId(), TouchType.LAST)
                .ifPresentOrElse(
                        lastTouch -> {
                            lastTouch.updateUtmData(utmData);
                            utmRepository.save(lastTouch);
                        },
                        () -> {
                            UtmAttribution lastTouch = UtmAttribution.captureLastTouch(
                                    session.getId(),
                                    visitor.getId(),
                                    utmData,
                                    now
                            );
                            utmRepository.save(lastTouch);
                        }
                );
    }
}
