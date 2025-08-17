package sn.msante.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("tenant_id")
    private String tenantId;
    
    @JsonProperty("is_verified")
    private Boolean isVerified;
    
    public AuthResponseDto(String accessToken, String refreshToken, Long expiresIn, 
                          String userId, String role, String tenantId, Boolean isVerified) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.role = role;
        this.tenantId = tenantId;
        this.isVerified = isVerified;
    }
}