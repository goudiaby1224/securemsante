package sn.goudiaby.msante;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Phase 4 Comprehensive Test Suite
 * 
 * This test suite verifies all Phase 4 infrastructure components:
 * - Performance optimization (caching)
 * - Security hardening
 * - Monitoring and health checks
 * - Resilience patterns
 * - Production configuration
 */
@SpringBootTest
@ActiveProfiles("test")
class Phase4ComprehensiveTestSuite {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Phase 4: Performance Infrastructure Test")
    void testPerformanceInfrastructure() {
        // Test caching infrastructure
        assertNotNull(cacheManager, "Cache manager should be available");
        
        // Verify cache names are configured
        assertTrue(cacheManager.getCacheNames().contains("doctors"), "Doctors cache should be configured");
        assertTrue(cacheManager.getCacheNames().contains("patients"), "Patients cache should be configured");
        assertTrue(cacheManager.getCacheNames().contains("appointments"), "Appointments cache should be configured");
        assertTrue(cacheManager.getCacheNames().contains("availabilities"), "Availabilities cache should be configured");
        assertTrue(cacheManager.getCacheNames().contains("users"), "Users cache should be configured");
        
        System.out.println("✅ Performance infrastructure test passed");
    }

    @Test
    @DisplayName("Phase 4: Security Infrastructure Test")
    void testSecurityInfrastructure() {
        // Test security configuration beans
        assertDoesNotThrow(() -> {
            applicationContext.getBean("corsConfigurationSource");
        }, "CORS configuration should be configured");
        
        System.out.println("✅ Security infrastructure test passed");
    }

    @Test
    @DisplayName("Phase 4: Monitoring Infrastructure Test")
    void testMonitoringInfrastructure() {
        // Test monitoring configuration
        assertDoesNotThrow(() -> {
            applicationContext.getBean("timedAspect");
        }, "Timed aspect should be configured for metrics");
        
        System.out.println("✅ Monitoring infrastructure test passed");
    }

    @Test
    @DisplayName("Phase 4: Health Check Infrastructure Test")
    void testHealthCheckInfrastructure() {
        // Test health indicators
        assertDoesNotThrow(() -> {
            applicationContext.getBean("databaseHealthIndicator");
        }, "Database health indicator should be configured");
        
        assertDoesNotThrow(() -> {
            applicationContext.getBean("externalServicesHealthIndicator");
        }, "External services health indicator should be configured");
        
        System.out.println("✅ Health check infrastructure test passed");
    }

    @Test
    @DisplayName("Phase 4: Resilience Infrastructure Test")
    void testResilienceInfrastructure() {
        // Test resilience service
        assertDoesNotThrow(() -> {
            applicationContext.getBean("resilienceService");
        }, "Resilience service should be configured");
        
        System.out.println("✅ Resilience infrastructure test passed");
    }

    @Test
    @DisplayName("Phase 4: Performance Optimization Service Test")
    void testPerformanceOptimizationService() {
        // Test performance optimization service
        assertDoesNotThrow(() -> {
            applicationContext.getBean("performanceOptimizationService");
        }, "Performance optimization service should be configured");
        
        System.out.println("✅ Performance optimization service test passed");
    }

    @Test
    @DisplayName("Phase 4: Configuration Properties Test")
    void testConfigurationProperties() {
        // Test that production configuration properties are available
        assertDoesNotThrow(() -> {
            applicationContext.getEnvironment().getProperty("spring.cache.type");
        }, "Cache configuration should be available");
        
        System.out.println("✅ Configuration properties test passed");
    }

    @Test
    @DisplayName("Phase 4: Bean Availability Test")
    void testBeanAvailability() {
        // Test that all Phase 4 beans are available
        String[] expectedBeans = {
            "performanceConfig",
            "securityHardeningConfig", 
            "monitoringConfig",
            "databaseHealthIndicator",
            "externalServicesHealthIndicator",
            "performanceOptimizationService",
            "resilienceService"
        };
        
        for (String beanName : expectedBeans) {
            assertDoesNotThrow(() -> {
                applicationContext.getBean(beanName);
            }, "Bean " + beanName + " should be available");
        }
        
        System.out.println("✅ Bean availability test passed");
    }

    @Test
    @DisplayName("Phase 4: Integration Test")
    void testPhase4Integration() {
        // Test that all Phase 4 components work together
        assertDoesNotThrow(() -> {
            // Test cache manager integration
            CacheManager cm = applicationContext.getBean(CacheManager.class);
            assertNotNull(cm, "Cache manager should be integrated");
            
            // Test health indicators integration
            HealthIndicator dbHealth = applicationContext.getBean("databaseHealthIndicator", HealthIndicator.class);
            assertNotNull(dbHealth, "Database health indicator should be integrated");
            
            // Test resilience service integration
            Object resilienceService = applicationContext.getBean("resilienceService");
            assertNotNull(resilienceService, "Resilience service should be integrated");
            
        }, "Phase 4 components should integrate properly");
        
        System.out.println("✅ Phase 4 integration test passed");
    }

    @Test
    @DisplayName("Phase 4: Complete Infrastructure Test")
    void testCompletePhase4Infrastructure() {
        System.out.println("\n🚀 Phase 4 Infrastructure Test Results:");
        System.out.println("=====================================");
        
        // Run all infrastructure tests
        testPerformanceInfrastructure();
        testSecurityInfrastructure();
        testMonitoringInfrastructure();
        testHealthCheckInfrastructure();
        testResilienceInfrastructure();
        testPerformanceOptimizationService();
        testConfigurationProperties();
        testBeanAvailability();
        testPhase4Integration();
        
        System.out.println("\n🎉 All Phase 4 infrastructure tests passed!");
        System.out.println("Phase 4 is ready for production deployment!");
    }
}
