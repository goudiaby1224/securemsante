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
    //last-name , first-name
    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;

    @NotBlank
    private String userType; // PATIENT or DOCTOR
    
    // Optional patient fields
    private String address;
    private String phone;
    private String birthDate; // yyyy-MM-dd format
    
    // Optional doctor fields
    private String specialty;
    private String licenseNumber;
    
    public User.Role getRoleEnum() {
        return User.Role.valueOf(userType.toUpperCase());
    }
}