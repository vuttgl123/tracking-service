package marketing.tracking_service.shared.infrastructure.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SystemClock {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
