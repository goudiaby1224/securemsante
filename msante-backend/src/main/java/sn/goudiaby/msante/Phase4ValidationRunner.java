package sn.goudiaby.msante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import sn.goudiaby.msante.service.Phase4ValidationService;
import sn.goudiaby.msante.service.Phase4ValidationService.Phase4ValidationResult;

/**
 * Simple runner to execute Phase 4 validation service
 * This allows us to run the validation without needing to run tests
 */
public class Phase4ValidationRunner {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Phase 4 Validation Runner...");
        System.out.println("================================================");
        
        try {
            // Start the Spring application context using the main application class
            ConfigurableApplicationContext context = SpringApplication.run(MSanteBackendApplication.class, args);
            
            // Wait a moment for the application to fully start
            Thread.sleep(5000);
            
            // Get the Phase 4 validation service
            Phase4ValidationService validationService = context.getBean(Phase4ValidationService.class);
            
            System.out.println("✅ Application started successfully!");
            System.out.println("🔍 Running Phase 4 validation...");
            System.out.println();
            
            // Run the complete Phase 4 validation
            Phase4ValidationResult result = validationService.runCompleteValidation();
            
            // Generate and display the validation report
            String report = validationService.generateValidationReport(result);
            System.out.println(report);
            
            // Display final status
            System.out.println();
            System.out.println("🎯 FINAL PHASE 4 VALIDATION STATUS: " + result.getOverallStatus());
            System.out.println("📊 Overall Success Rate: " + String.format("%.2f%%", result.getOverallSuccessRate()));
            System.out.println("✅ Tests Passed: " + result.getSuccessfulTests() + "/" + result.getTotalTests());
            
            if (result.getOverallSuccessRate() >= 90.0) {
                System.out.println("🎉 PHASE 4 IS COMPLETE AND PRODUCTION READY!");
            } else if (result.getOverallSuccessRate() >= 80.0) {
                System.out.println("⚠️  PHASE 4 IS MOSTLY READY BUT HAS SOME ISSUES");
            } else {
                System.out.println("❌ PHASE 4 HAS SIGNIFICANT ISSUES THAT NEED RESOLUTION");
            }
            
            // Shutdown the application
            context.close();
            
        } catch (Exception e) {
            System.err.println("❌ Phase 4 validation failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
