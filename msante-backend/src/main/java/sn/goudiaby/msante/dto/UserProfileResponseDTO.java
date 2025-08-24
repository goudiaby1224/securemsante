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
    private LocalDate dateOfBirth;
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
            dto.setDateOfBirth(user.getPatient().getDateOfBirth());
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
    
    // Manual getters and setters to ensure compilation works
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    
    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }
    
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
