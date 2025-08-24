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
    
    // Manual setters to ensure compilation works
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setAddress(String address) { this.address = address; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    // Alias method for backward compatibility with tests
    public void setRole(String role) { this.userType = role; }
}