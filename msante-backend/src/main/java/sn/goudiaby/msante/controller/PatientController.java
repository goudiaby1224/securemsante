package sn.goudiaby.msante.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.dto.PatientProfileDTO;
import sn.goudiaby.msante.service.PatientService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Patient Management", description = "Endpoints for patient profile and medical history management")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get current patient's profile", description = "Retrieve the profile of the currently authenticated patient")
    public ResponseEntity<PatientProfileDTO> getCurrentPatientProfile() {
        return ResponseEntity.ok(patientService.getCurrentPatientProfile());
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Update patient's profile", description = "Update the profile of the currently authenticated patient")
    public ResponseEntity<PatientProfileDTO> updatePatientProfile(@Valid @RequestBody PatientProfileDTO profileDTO) {
        return ResponseEntity.ok(patientService.updatePatientProfile(profileDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get patient by ID", description = "Retrieve patient profile by ID (doctors only)")
    public ResponseEntity<PatientProfileDTO> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/profile/medical-history")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get patient's medical history", description = "Retrieve the medical history of the current patient")
    public ResponseEntity<Object> getMedicalHistory() {
        return ResponseEntity.ok(patientService.getMedicalHistory());
    }

    @PostMapping("/profile/allergies")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Add allergy", description = "Add a new allergy to patient's medical record")
    public ResponseEntity<String> addAllergy(@RequestParam String allergy) {
        return ResponseEntity.ok(patientService.addAllergy(allergy));
    }

    @DeleteMapping("/profile/allergies/{allergy}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Remove allergy", description = "Remove an allergy from patient's medical record")
    public ResponseEntity<String> removeAllergy(@PathVariable String allergy) {
        return ResponseEntity.ok(patientService.removeAllergy(allergy));
    }

    @PostMapping("/profile/medical-conditions")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Add medical condition", description = "Add a new medical condition to patient's record")
    public ResponseEntity<String> addMedicalCondition(@RequestParam String condition) {
        return ResponseEntity.ok(patientService.addMedicalCondition(condition));
    }

    @DeleteMapping("/profile/medical-conditions/{condition}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Remove medical condition", description = "Remove a medical condition from patient's record")
    public ResponseEntity<String> removeMedicalCondition(@PathVariable String condition) {
        return ResponseEntity.ok(patientService.removeMedicalCondition(condition));
    }

    @PostMapping("/profile/medications")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Add medication", description = "Add a new medication to patient's record")
    public ResponseEntity<String> addMedication(@RequestParam String medication) {
        return ResponseEntity.ok(patientService.addMedication(medication));
    }

    @DeleteMapping("/profile/medications/{medication}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Remove medication", description = "Remove a medication from patient's record")
    public ResponseEntity<String> removeMedication(@PathVariable String medication) {
        return ResponseEntity.ok(patientService.removeMedication(medication));
    }

    @GetMapping("/profile/emergency-contacts")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get emergency contacts", description = "Retrieve patient's emergency contact information")
    public ResponseEntity<Object> getEmergencyContacts() {
        return ResponseEntity.ok(patientService.getEmergencyContacts());
    }

    @PutMapping("/profile/emergency-contacts")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Update emergency contacts", description = "Update patient's emergency contact information")
    public ResponseEntity<String> updateEmergencyContacts(@RequestParam String contact, @RequestParam String phone) {
        return ResponseEntity.ok(patientService.updateEmergencyContacts(contact, phone));
    }
}
