package sn.goudiaby.msante.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    
    private Long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String userType;
    private Boolean isActive;
    private String createdAt;


}