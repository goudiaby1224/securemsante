package sn.goudiaby.msante.dto;

import lombok.Data;
import sn.goudiaby.msante.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserProfileResponseDTO {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Patient-specific fields
    private String address;
    private LocalDate birthDate;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    
    // Doctor-specific fields
    private String specialty;
    private String licenseNumber;
    private String department;
    
    public static UserProfileResponseDTO fromUser(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        // Set patient-specific fields if user is a patient
        if (user.getRole() == User.Role.PATIENT && user.getPatient() != null) {
            dto.setAddress(user.getPatient().getAddress());
            dto.setBirthDate(user.getPatient().getBirthDate());
            // Note: Emergency contact fields would need to be added to Patient model
        }
        
        // Set doctor-specific fields if user is a doctor
        if (user.getRole() == User.Role.DOCTOR && user.getDoctor() != null) {
            dto.setSpecialty(user.getDoctor().getSpecialty());
            dto.setLicenseNumber(user.getDoctor().getLicenseNumber());
            // Note: Department field would need to be added to Doctor model
        }
        
        return dto;
    }
}
