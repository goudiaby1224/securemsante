package sn.goudiaby.msante.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.constants.ApplicationConstants;
import sn.goudiaby.msante.dto.LoginRequestDTO;
import sn.goudiaby.msante.dto.LoginResponseDTO;
import sn.goudiaby.msante.dto.RegisterRequestDTO;
import sn.goudiaby.msante.dto.UserResponseDTO;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.service.AuthService;
import sn.goudiaby.msante.service.UserService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://localhost:4200"})
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        userService.registerUser(request); // hash + save
        return ResponseEntity.ok(authService.authenticate(request.getEmail(), request.getPassword()));
    }


    //get user profile
    @GetMapping("/login")
    public ResponseEntity<UserResponseDTO> getUserProfile(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByEmail(authentication.getName());
            // Convert User to UserResponseDTO
            UserResponseDTO userResponseDTO = new UserResponseDTO(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhone(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.isEnabled(),
                    user.getCreatedAt().toString()
            );
            return ResponseEntity.ok(userResponseDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/apiLogin")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.authenticate(request.getEmail(), request.getPassword());

        if (response.getUser() != null && response.getToken() != null) {
            return ResponseEntity.ok()
                    .header(ApplicationConstants.JWT_HEADER, response.getToken())
                    .body(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Boolean>> logout() {
        // Aucun traitement côté serveur en stateless
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}