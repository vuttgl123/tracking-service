package marketing.tracking_service.tracking.infrastructure.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import marketing.tracking_service.tracking.domain.model.attribution.UtmAttributionRepository;
import marketing.tracking_service.tracking.domain.model.attribution.UtmData;
import marketing.tracking_service.tracking.domain.model.session.SessionId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class UtmRepositoryAdapter implements UtmAttributionRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void upsertFirstTouch(SessionId sessionId, UtmData data, Instant at) {
        em.createNativeQuery("""
      INSERT IGNORE INTO utm_attributions
        (session_id, touch_type, utm_source, utm_medium, utm_campaign, utm_term, utm_content, click_id, captured_at)
      VALUES
        (?, 'first', ?, ?, ?, ?, ?, ?, ?)
      """)
                .setParameter(1, sessionId.value())
                .setParameter(2, data.source())
                .setParameter(3, data.medium())
                .setParameter(4, data.campaign())
                .setParameter(5, data.term())
                .setParameter(6, data.content())
                .setParameter(7, data.clickId())
                .setParameter(8, Timestamp.from(at))
                .executeUpdate();
    }

    @Override
    @Transactional
    public void upsertLastTouch(SessionId sessionId, UtmData data, Instant at) {
        em.createNativeQuery("""
      INSERT INTO utm_attributions
        (session_id, touch_type, utm_source, utm_medium, utm_campaign, utm_term, utm_content, click_id, captured_at)
      VALUES
        (?, 'last', ?, ?, ?, ?, ?, ?, ?)
      ON DUPLICATE KEY UPDATE
        utm_source = VALUES(utm_source),
        utm_medium = VALUES(utm_medium),
        utm_campaign = VALUES(utm_campaign),
        utm_term = VALUES(utm_term),
        utm_content = VALUES(utm_content),
        click_id = VALUES(click_id),
        captured_at = VALUES(captured_at)
      """)
                .setParameter(1, sessionId.value())
                .setParameter(2, data.source())
                .setParameter(3, data.medium())
                .setParameter(4, data.campaign())
                .setParameter(5, data.term())
                .setParameter(6, data.content())
                .setParameter(7, data.clickId())
                .setParameter(8, Timestamp.from(at))
                .executeUpdate();
    }
}
