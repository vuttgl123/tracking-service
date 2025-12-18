package marketing.tracking_service.tracking.application.command.captureutm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marketing.tracking_service.shared.application.CommandHandler;
import marketing.tracking_service.tracking.domain.model.attribution.*;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import marketing.tracking_service.tracking.domain.model.visitor.VisitorId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
class CaptureUtmHandler implements CommandHandler<CaptureUtmCommand, CaptureUtmResult> {
    private final UtmAttributionRepository utmRepository;
    private final Clock clock;

    @Override
    @Transactional
    public CaptureUtmResult handle(CaptureUtmCommand cmd) {
        VisitorId visitorId = VisitorId.from(cmd.visitorId());
        SessionId sessionId = SessionId.from(cmd.sessionId());

        UtmData utmData = UtmData.builder()
                .utmSource(cmd.utmSource())
                .utmMedium(cmd.utmMedium())
                .utmCampaign(cmd.utmCampaign())
                .utmContent(cmd.utmContent())
                .utmTerm(cmd.utmTerm())
                .clickIds(ClickIds.builder()
                        .genericClickId(cmd.clickId())
                        .gclid(cmd.gclid())
                        .fbclid(cmd.fbclid())
                        .build())
                .referrerDomain(cmd.referrerDomain())
                .landingPage(cmd.landingPage())
                .build();

        Instant now = clock.instant();

        boolean firstTouchCaptured = captureFirstTouch(sessionId, visitorId, utmData, now);
        boolean lastTouchCaptured = captureLastTouch(sessionId, visitorId, utmData, now);

        log.info("UTM captured: sessionId={}, firstTouch={}, lastTouch={}", sessionId, firstTouchCaptured, lastTouchCaptured);
        return new CaptureUtmResult(firstTouchCaptured, lastTouchCaptured);
    }

    private boolean captureFirstTouch(SessionId sessionId, VisitorId visitorId, UtmData utmData, Instant now) {
        if (utmRepository.findByVisitorAndTouchType(visitorId, TouchType.FIRST).isPresent()) {
            return false;
        }

        UtmAttribution firstTouch = UtmAttribution.captureFirstTouch(sessionId, visitorId, utmData, now);
        utmRepository.save(firstTouch);
        return true;
    }

    private boolean captureLastTouch(SessionId sessionId, VisitorId visitorId, UtmData utmData, Instant now) {
        utmRepository.findBySessionAndTouchType(sessionId, TouchType.LAST)
                .map(lastTouch -> {
                    lastTouch.updateUtmData(utmData);
                    utmRepository.save(lastTouch);
                    return true;
                })
                .orElseGet(() -> {
                    UtmAttribution lastTouch = UtmAttribution.captureLastTouch(
                            sessionId,
                            visitorId,
                            utmData,
                            now
                    );
                    utmRepository.save(lastTouch);
                    return true;
                });
        return true;
    }
}
