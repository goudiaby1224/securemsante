package sn.msante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * OTP verification DTO for authentication
 */
@Data
public class OtpVerificationDto {
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @NotBlank(message = "OTP code is required")
    @Size(min = 4, max = 8, message = "OTP code must be between 4 and 8 characters")
    private String code;
    
    private String tenantId;
}