package sn.goudiaby.msante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequestDTO {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @Pattern(regexp = "^\\+221\\s?[0-9]{2}\\s?[0-9]{3}\\s?[0-9]{2}\\s?[0-9]{2}$", 
             message = "Phone number must be in Senegalese format: +221 XX XXX XX XX")
    private String phone;
    
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
    
    private LocalDate birthDate;
    
    // Optional fields for specific user types
    private String specialty; // For doctors
    private String licenseNumber; // For doctors
    private String emergencyContactName; // For patients
    private String emergencyContactPhone; // For patients
    private String emergencyContactRelationship; // For patients
}
