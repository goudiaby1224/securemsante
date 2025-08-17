package sn.msante.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.msante.dto.AuthResponseDto;
import sn.msante.dto.OtpRequestDto;
import sn.msante.dto.OtpVerificationDto;
import sn.msante.service.AuthenticationService;

/**
 * Authentication controller for OTP-based authentication
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "OTP-based authentication endpoints")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/otp")
    @Operation(summary = "Request OTP", description = "Send OTP code to phone number via SMS or WhatsApp")
    public ResponseEntity<Void> requestOtp(@Valid @RequestBody OtpRequestDto request) {
        authenticationService.sendOtp(request);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verify OTP code and get JWT tokens")
    public ResponseEntity<AuthResponseDto> verifyOtp(@Valid @RequestBody OtpVerificationDto request) {
        AuthResponseDto response = authenticationService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Get new access token using refresh token")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        // Remove "Bearer " prefix if present
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        
        AuthResponseDto response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}