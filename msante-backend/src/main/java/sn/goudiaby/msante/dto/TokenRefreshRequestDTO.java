package sn.goudiaby.msante.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequestDTO {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    // Manual getters and setters to ensure compilation works
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
