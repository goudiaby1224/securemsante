package sn.goudiaby.msante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    // Manual getters to ensure compilation works
    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
}
