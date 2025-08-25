package sn.goudiaby.msante;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sn.goudiaby.msante.service.Phase4ValidationService;
import sn.goudiaby.msante.service.Phase4ValidationService.Phase4ValidationResult;

/**
 * Test class to execute Phase 4 validation service
 * This will run all comprehensive tests and provide a final status report
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.cache.type=simple"
})
public class Phase4ValidationTest {

    @Autowired
    private Phase4ValidationService phase4ValidationService;

    @Test
    void testPhase4CompleteValidation() {
        System.out.println("🚀 Starting Phase 4 Complete Validation...");
        System.out.println("================================================");
        
        try {
            // Run the complete Phase 4 validation
            Phase4ValidationResult result = phase4ValidationService.runCompleteValidation();
            
            // Generate and display the validation report
            String report = phase4ValidationService.generateValidationReport(result);
            System.out.println(report);
            
            // Assert that the validation passed
            assert result.getOverallSuccessRate() >= 80.0 : 
                "Phase 4 validation failed with success rate: " + result.getOverallSuccessRate() + "%";
            
            System.out.println("✅ Phase 4 validation completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Phase 4 validation failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
