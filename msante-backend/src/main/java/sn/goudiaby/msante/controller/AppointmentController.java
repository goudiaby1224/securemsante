package sn.goudiaby.msante.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.dto.*;
import sn.goudiaby.msante.service.AppointmentService;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:4200", "https://localhost:4200"})
@Tag(name = "Appointment", description = "Endpoints for appointment management")
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Manual constructor to ensure compilation works
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(@Valid @RequestBody BookAppointmentRequestDTO request) {
        try {
            AppointmentResponseDTO appointment = appointmentService.bookAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponseDTO>> getPatientAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getPatientAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AppointmentResponseDTO>> getDoctorAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.getDoctorAppointments();
        return ResponseEntity.ok(appointments);
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.ok("Appointment cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to cancel appointment: " + e.getMessage());
        }
    }
    
    @PostMapping("/search")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AppointmentResponseDTO>> searchAvailableSlots(@Valid @RequestBody SearchAvailabilityRequestDTO request) {
        try {
            List<AppointmentResponseDTO> availableSlots = appointmentService.searchAvailableSlots(request);
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    @PostMapping("/{id}/reschedule")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AppointmentResponseDTO> rescheduleAppointment(
            @PathVariable("id") Long appointmentId,
            @RequestParam("newAvailabilityId") Long newAvailabilityId) {
        try {
            AppointmentResponseDTO appointment = appointmentService.rescheduleAppointment(appointmentId, newAvailabilityId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
    
    @GetMapping("/upcoming")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AppointmentResponseDTO>> getUpcomingAppointments() {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingAppointments();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
}