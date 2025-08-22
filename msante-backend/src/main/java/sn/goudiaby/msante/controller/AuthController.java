package sn.goudiaby.msante.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.dto.LoginRequestDTO;
import sn.goudiaby.msante.dto.LoginResponseDTO;
import sn.goudiaby.msante.dto.RegisterRequestDTO;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://localhost:4200"})
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully with email: " + user.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Registration failed: " + e.getMessage());
        }
    }



    //get user profile
    @GetMapping("/login")
    public ResponseEntity<User> getUserProfile(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByEmail(authentication.getName());
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}