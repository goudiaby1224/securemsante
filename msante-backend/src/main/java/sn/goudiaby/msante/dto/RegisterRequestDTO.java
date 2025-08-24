package sn.goudiaby.msante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import sn.goudiaby.msante.model.User;

@Data
public class RegisterRequestDTO {
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String lastName;
    
    @NotBlank
    private String firstName;

    @NotBlank
    private String phone;
    
    @NotBlank
    private String userType; // PATIENT or DOCTOR
    
    // Optional patient fields
    private String address;
    private String dateOfBirth; // yyyy-MM-dd format - matches Patient model
    
    // Optional doctor fields
    private String specialty;
    private String licenseNumber;
    
    public User.Role getRoleEnum() {
        return User.Role.valueOf(userType.toUpperCase());
    }
    
    // Manual getters to ensure compilation works
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getPhone() { return phone; }
    public String getUserType() { return userType; }
    public String getAddress() { return address; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getSpecialty() { return specialty; }
    public String getLicenseNumber() { return licenseNumber; }
}