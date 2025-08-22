package sn.goudiaby.msante.dto;

public class LoginResponseDTO {
    private UserResponseDTO user;
    private String token;

    public LoginResponseDTO(UserResponseDTO user, String token) {
        this.user = user;
        this.token = token;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
