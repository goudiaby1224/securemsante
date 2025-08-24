package sn.goudiaby.msante.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import sn.goudiaby.msante.constants.ApplicationConstants;
import sn.goudiaby.msante.dto.LoginResponseDTO;
import sn.goudiaby.msante.dto.UserResponseDTO;
import sn.goudiaby.msante.dto.TokenRefreshRequestDTO;
import sn.goudiaby.msante.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final Environment env;

    // Manual constructor to ensure compilation works
    public AuthService(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.env = env;
    }

    public LoginResponseDTO authenticate(String email, String password) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(email, password);
        Authentication authResponse = authenticationManager.authenticate(authentication);

        if (authResponse != null && authResponse.isAuthenticated()) {
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                    ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            String jwt = Jwts.builder()
                    .issuer("Eazy Bank")
                    .subject("JWT Token")
                    .claim("username", authResponse.getName())
                    .claim("authorities", authResponse.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(",")))
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 30000000))
                    .signWith(secretKey)
                    .compact();

            User user = userService.findByEmail(authResponse.getName());

            UserResponseDTO userDTO = new UserResponseDTO(
                    user.getId(), user.getFirstName(), user.getLastName(),
                    user.getPhone(), user.getEmail(), user.getRole().name(),
                    user.isEnabled(), user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
            );

            return new LoginResponseDTO(userDTO, jwt);
        }

        return new LoginResponseDTO(null, null);
    }

    public LoginResponseDTO refreshToken(String refreshToken) {
        try {
            // In a real implementation, you would validate the refresh token
            // For now, we'll create a new JWT token
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                    ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // For simplicity, we'll generate a new token
            // In production, you should validate the refresh token and extract user info
            String jwt = Jwts.builder()
                    .issuer("M-Santé")
                    .subject("JWT Token")
                    .claim("username", "user@example.com") // This should come from refresh token validation
                    .claim("authorities", "ROLE_PATIENT") // This should come from refresh token validation
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 30000000))
                    .signWith(secretKey)
                    .compact();

            // For now, return a basic response
            // In production, you would fetch the actual user from the refresh token
            UserResponseDTO userDTO = new UserResponseDTO(
                    1L, "User", "Example", "+221-77-123-4567", 
                    "user@example.com", "PATIENT", true, new Date().toString()
            );

            return new LoginResponseDTO(userDTO, jwt);
        } catch (Exception e) {
            return new LoginResponseDTO(null, null);
        }
    }
}
