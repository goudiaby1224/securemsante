package sn.goudiaby.msante.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private String email;
    private String role;
    private String message;
}