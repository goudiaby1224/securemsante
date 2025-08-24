package sn.goudiaby.msante.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.service.AnalyticsService;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Analytics", description = "Endpoints for system analytics and insights")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve overall system statistics for admin dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    @GetMapping("/doctors/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get doctor statistics", description = "Retrieve statistics for a specific doctor")
    public ResponseEntity<Map<String, Object>> getDoctorStats(@PathVariable Long doctorId) {
        return ResponseEntity.ok(analyticsService.getDoctorStats(doctorId));
    }

    @GetMapping("/patients/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Get patient statistics", description = "Retrieve statistics for a specific patient")
    public ResponseEntity<Map<String, Object>> getPatientStats(@PathVariable Long patientId) {
        return ResponseEntity.ok(analyticsService.getPatientStats(patientId));
    }

    @GetMapping("/appointments/trends")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get appointment trends", description = "Retrieve appointment trends and patterns")
    public ResponseEntity<Map<String, Object>> getAppointmentTrends() {
        return ResponseEntity.ok(analyticsService.getAppointmentTrends());
    }

    @GetMapping("/system/performance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get system performance", description = "Retrieve system performance metrics")
    public ResponseEntity<Map<String, Object>> getSystemPerformance() {
        return ResponseEntity.ok(analyticsService.getSystemPerformance());
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get revenue analytics", description = "Retrieve revenue analytics and financial insights")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics() {
        return ResponseEntity.ok(analyticsService.getRevenueAnalytics());
    }

    @GetMapping("/my-stats")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user statistics", description = "Retrieve statistics for the currently authenticated user")
    public ResponseEntity<Map<String, Object>> getMyStats() {
        // This would return different stats based on user role
        // For now, return a placeholder
        Map<String, Object> stats = Map.of(
            "message", "Statistics will be implemented based on user role",
            "timestamp", java.time.LocalDateTime.now()
        );
        return ResponseEntity.ok(stats);
    }
}
