package sn.goudiaby.msante.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.dto.CreateAvailabilityRequestDTO;
import sn.goudiaby.msante.model.Availability;
import sn.goudiaby.msante.service.AvailabilityService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = {"http://localhost:4200", "https://localhost:4200"})
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    // Manual constructor to ensure compilation works
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Availability> createAvailability(@Valid @RequestBody CreateAvailabilityRequestDTO request) {
        try {
            Availability availability = availabilityService.createAvailability(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(availability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<Availability>> getDoctorAvailability() {
        try {
            List<Availability> availabilities = availabilityService.getCurrentDoctorAvailability();
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> deleteAvailability(@PathVariable Long id) {
        try {
            availabilityService.deleteAvailability(id);
            return ResponseEntity.ok("Availability slot deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to delete availability: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Availability> blockAvailability(@PathVariable Long id) {
        try {
            Availability availability = availabilityService.blockAvailability(id);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}