package marketing.tracking_service.tracking.application.command.startsession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.shared.application.CommandHandler;
import marketing.tracking_service.shared.domain.DomainEventPublisher;
import marketing.tracking_service.shared.domain.IdGenerator;
import marketing.tracking_service.tracking.domain.model.session.*;
import marketing.tracking_service.tracking.domain.model.visitor.Visitor;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorRepository;
import marketing.tracking_service.tracking.domain.service.SessionLifecycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;


@Slf4j
@Service
@RequiredArgsConstructor
public class StartSessionHandler implements CommandHandler<StartSessionCommand, StartSessionResult> {

    private final IdGenerator idGenerator;
    private final Clock clock;
    private final VisitorRepository visitorRepository;
    private final SessionRepository sessionRepository;
    private final SessionLifecycleService sessionLifecycle;
    private final DomainEventPublisher eventPublisher;
    private final DeviceDetectionService deviceDetection;

    @Override
    @Transactional
    public StartSessionResult handle(StartSessionCommand cmd) {
        VisitorId visitorId = resolveVisitorId(cmd.visitorId());
        SessionId sessionId = resolveSessionId(cmd.sessionId());

        Visitor visitor = getOrCreateVisitor(visitorId, cmd);

        IpHash ipHash = cmd.ipAddress() != null ? IpHash.fromIpAddress(cmd.ipAddress()) : null;
        DeviceInfo deviceInfo = deviceDetection.detect(cmd.userAgent(), cmd.screenWidth(), cmd.screenHeight(), cmd.language(), cmd.timezone());
        Session session = sessionLifecycle.getOrCreateSession(sessionId, visitorId, ipHash, cmd.userAgent(), deviceInfo, cmd.referrerUrl(), cmd.landingUrl());

        eventPublisher.publishFrom(visitor);
        eventPublisher.publishFrom(session);

        log.info("Session started: sessionId={}, visitorId={}", session.getId(), visitor.getId());
        return new StartSessionResult(visitor.getId().value(), session.getId().value(), session.getStartedAt());
    }

    private VisitorId resolveVisitorId(String visitorIdStr) {
        VisitorId visitorId = VisitorId.fromOrNull(visitorIdStr);
        return visitorId != null ? visitorId : VisitorId.from(idGenerator.newId());
    }

    private SessionId resolveSessionId(String sessionIdStr) {
        SessionId sessionId = SessionId.fromOrNull(sessionIdStr);
        return sessionId != null ? sessionId : SessionId.from(idGenerator.newId());
    }

    private Visitor getOrCreateVisitor(VisitorId visitorId, StartSessionCommand cmd) {
        return visitorRepository.findById(visitorId)
                .map(visitor -> {
                    visitor.markAsSeen();
                    return visitorRepository.save(visitor);
                })
                .orElseGet(() -> {
                    Visitor newVisitor = Visitor.create(
                            visitorId,
                            clock.instant(),
                            cmd.referrerUrl(),
                            cmd.landingUrl(),
                            cmd.userAgent()
                    );
                    return visitorRepository.save(newVisitor);
                });
    }
}