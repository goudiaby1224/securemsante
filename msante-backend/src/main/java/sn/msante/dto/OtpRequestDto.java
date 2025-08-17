package sn.msante.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * OTP request DTO for authentication
 */
@Data
public class OtpRequestDto {
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
    
    @JsonProperty("channel")
    @Pattern(regexp = "^(sms|whatsapp)$", message = "Channel must be 'sms' or 'whatsapp'")
    private String channel = "sms";
    
    @JsonProperty("tenant_id")
    private String tenantId;
}