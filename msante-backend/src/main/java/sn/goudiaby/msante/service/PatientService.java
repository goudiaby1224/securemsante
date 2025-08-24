package sn.goudiaby.msante.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.goudiaby.msante.dto.PatientProfileDTO;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.UserRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientProfileDTO getCurrentPatientProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        return convertToDTO(patient);
    }

    public PatientProfileDTO updatePatientProfile(PatientProfileDTO profileDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        // Update patient fields
        patient.setFirstName(profileDTO.getFirstName());
        patient.setLastName(profileDTO.getLastName());
        patient.setPhone(profileDTO.getPhone());
        patient.setDateOfBirth(profileDTO.getDateOfBirth());
        patient.setGender(profileDTO.getGender());
        patient.setBloodType(profileDTO.getBloodType());
        patient.setEmergencyContact(profileDTO.getEmergencyContact());
        patient.setEmergencyPhone(profileDTO.getEmergencyPhone());
        patient.setAddress(profileDTO.getAddress());
        patient.setCity(profileDTO.getCity());
        patient.setCountry(profileDTO.getCountry());
        patient.setInsuranceProvider(profileDTO.getInsuranceProvider());
        patient.setInsuranceNumber(profileDTO.getInsuranceNumber());
        patient.setMedicalHistory(profileDTO.getMedicalHistory());
        patient.setPreferences(profileDTO.getPreferences());
        
        Patient savedPatient = patientRepository.save(patient);
        return convertToDTO(savedPatient);
    }

    public PatientProfileDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return convertToDTO(patient);
    }

    public Object getMedicalHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        return Map.of(
            "allergies", patient.getAllergies() != null ? patient.getAllergies() : new ArrayList<>(),
            "medicalConditions", patient.getMedicalConditions() != null ? patient.getMedicalConditions() : new ArrayList<>(),
            "medications", patient.getMedications() != null ? patient.getMedications() : new ArrayList<>(),
            "medicalHistory", patient.getMedicalHistory()
        );
    }

    public String addAllergy(String allergy) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        if (patient.getAllergies() == null) {
            patient.setAllergies(new ArrayList<>());
        }
        patient.getAllergies().add(allergy);
        patientRepository.save(patient);
        
        return "Allergy added successfully";
    }

    public String removeAllergy(String allergy) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        if (patient.getAllergies() != null) {
            patient.getAllergies().remove(allergy);
            patientRepository.save(patient);
        }
        
        return "Allergy removed successfully";
    }

    public String addMedicalCondition(String condition) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        if (patient.getMedicalConditions() == null) {
            patient.setMedicalConditions(new ArrayList<>());
        }
        patient.getMedicalConditions().add(condition);
        patientRepository.save(patient);
        
        return "Medical condition added successfully";
    }

    public String removeMedicalCondition(String condition) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        if (patient.getMedicalConditions() != null) {
            patient.getMedicalConditions().remove(condition);
            patientRepository.save(patient);
        }
        
        return "Medical condition removed successfully";
    }

    public String addMedication(String medication) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        if (patient.getMedications() == null) {
            patient.setMedications(new ArrayList<>());
        }
        patient.getMedications().add(medication);
        patientRepository.save(patient);
        
        return "Medication added successfully";
    }

    public String removeMedication(String medication) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        if (patient.getMedications() != null) {
            patient.getMedications().remove(medication);
            patientRepository.save(patient);
        }
        
        return "Medication removed successfully";
    }

    public Object getEmergencyContacts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        return Map.of(
            "emergencyContact", patient.getEmergencyContact(),
            "emergencyPhone", patient.getEmergencyPhone()
        );
    }

    public String updateEmergencyContacts(String contact, String phone) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        
        patient.setEmergencyContact(contact);
        patient.setEmergencyPhone(phone);
        patientRepository.save(patient);
        
        return "Emergency contacts updated successfully";
    }

    private PatientProfileDTO convertToDTO(Patient patient) {
        PatientProfileDTO dto = new PatientProfileDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getUser().getEmail());
        dto.setPhone(patient.getPhone());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setBloodType(patient.getBloodType());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setEmergencyPhone(patient.getEmergencyPhone());
        dto.setAddress(patient.getAddress());
        dto.setCity(patient.getCity());
        dto.setCountry(patient.getCountry());
        dto.setInsuranceProvider(patient.getInsuranceProvider());
        dto.setInsuranceNumber(patient.getInsuranceNumber());
        dto.setAllergies(patient.getAllergies());
        dto.setMedicalConditions(patient.getMedicalConditions());
        dto.setMedications(patient.getMedications());
        dto.setMedicalHistory(patient.getMedicalHistory());
        dto.setPreferences(patient.getPreferences());
        dto.setProfileImage(patient.getProfileImage());
        dto.setIsActive(patient.getIsActive());
        
        return dto;
    }
}
