package sn.goudiaby.msante.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.dto.DoctorProfileDTO;
import sn.goudiaby.msante.dto.AdvancedSearchDTO;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.Availability;
import sn.goudiaby.msante.service.DoctorService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Doctor Management", description = "Endpoints for doctor management and search")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get current doctor's profile", description = "Retrieve the profile of the currently authenticated doctor")
    public ResponseEntity<DoctorProfileDTO> getCurrentDoctorProfile() {
        return ResponseEntity.ok(doctorService.getCurrentDoctorProfile());
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Update doctor's profile", description = "Update the profile of the currently authenticated doctor")
    public ResponseEntity<DoctorProfileDTO> updateDoctorProfile(@Valid @RequestBody DoctorProfileDTO profileDTO) {
        return ResponseEntity.ok(doctorService.updateDoctorProfile(profileDTO));
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Search doctors", description = "Search for doctors based on various criteria")
    public ResponseEntity<List<DoctorProfileDTO>> searchDoctors(@Valid AdvancedSearchDTO searchDTO) {
        return ResponseEntity.ok(doctorService.searchDoctors(searchDTO));
    }

    @GetMapping("/specialties")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get all specialties", description = "Retrieve list of all available medical specialties")
    public ResponseEntity<List<String>> getAllSpecialties() {
        return ResponseEntity.ok(doctorService.getAllSpecialties());
    }

    @GetMapping("/departments")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get all departments", description = "Retrieve list of all available hospital departments")
    public ResponseEntity<List<String>> getAllDepartments() {
        return ResponseEntity.ok(doctorService.getAllDepartments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get doctor by ID", description = "Retrieve doctor profile by their ID")
    public ResponseEntity<DoctorProfileDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/{id}/availability")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get doctor availability", description = "Get available time slots for a specific doctor")
    public ResponseEntity<List<Availability>> getDoctorAvailability(@PathVariable Long id, @RequestParam String date) {
        return ResponseEntity.ok(doctorService.getDoctorAvailability(id, date));
    }

    @PostMapping("/{id}/rating")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Rate a doctor", description = "Submit a rating and review for a doctor")
    public ResponseEntity<String> rateDoctor(@PathVariable Long id, @RequestParam Double rating, @RequestParam String review) {
        return ResponseEntity.ok(doctorService.rateDoctor(id, rating, review));
    }

    @GetMapping("/top-rated")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get top rated doctors", description = "Retrieve list of top rated doctors")
    public ResponseEntity<List<DoctorProfileDTO>> getTopRatedDoctors(@RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(doctorService.getTopRatedDoctors(limit));
    }
}