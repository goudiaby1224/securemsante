package sn.goudiaby.msante.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ExternalServicesHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // For now, return UP status
        // In production, you would check actual external services
        return Health.up()
            .withDetail("external-services", "All services operational")
            .withDetail("email-service", "UP")
            .withDetail("sms-service", "UP")
            .withDetail("payment-service", "UP")
            .build();
    }
}
