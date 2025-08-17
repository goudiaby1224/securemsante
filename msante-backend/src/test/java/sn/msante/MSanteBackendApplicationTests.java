package sn.msante;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test to verify application context loads
 */
@SpringBootTest
@ActiveProfiles("dev")
class MSanteBackendApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring Boot application context loads without errors
        // This validates all beans are correctly configured and dependencies resolved
    }
}