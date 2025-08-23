package sn.goudiaby.msante.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.dto.ChangePasswordRequestDTO;
import sn.goudiaby.msante.dto.UpdateProfileRequestDTO;
import sn.goudiaby.msante.dto.UserProfileResponseDTO;
import sn.goudiaby.msante.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://localhost:4200"})
@Tag(name = "User Management", description = "Endpoints for user profile management")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
    public ResponseEntity<UserProfileResponseDTO> getCurrentUserProfile(Authentication authentication) {
        UserProfileResponseDTO profile = userService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update current user profile", description = "Update the profile of the currently authenticated user")
    public ResponseEntity<UserProfileResponseDTO> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequestDTO updateRequest) {
        UserProfileResponseDTO updatedProfile = userService.updateCurrentUserProfile(authentication.getName(), updateRequest);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change user password", description = "Change the password of the currently authenticated user")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequest) {
        userService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Get user profile by ID", description = "Retrieve a user profile by ID (doctors only)")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileById(@PathVariable Long userId) {
        UserProfileResponseDTO profile = userService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }
}
