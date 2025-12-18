package marketing.tracking_service.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "session_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "session_id", length = 26, nullable = false)
    private String sessionId;

    @Column(name = "activity_at", nullable = false)
    private Instant activityAt;

    @Column(name = "page_url", length = 500)
    private String pageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}