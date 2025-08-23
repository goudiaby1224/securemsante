package sn.goudiaby.msante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import sn.goudiaby.msante.dto.ChangePasswordRequestDTO;
import sn.goudiaby.msante.dto.UpdateProfileRequestDTO;
import sn.goudiaby.msante.dto.UserProfileResponseDTO;
import sn.goudiaby.msante.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfileResponseDTO mockUserProfile;
    private UpdateProfileRequestDTO mockUpdateRequest;
    private ChangePasswordRequestDTO mockChangePasswordRequest;

    @BeforeEach
    void setUp() {
        mockUserProfile = new UserProfileResponseDTO();
        mockUserProfile.setId(1L);
        mockUserProfile.setEmail("test@example.com");
        mockUserProfile.setFirstName("John");
        mockUserProfile.setLastName("Doe");
        mockUserProfile.setPhone("+221-77-123-4567");
        mockUserProfile.setRole("PATIENT");
        mockUserProfile.setEnabled(true);
        mockUserProfile.setCreatedAt(LocalDateTime.now());
        mockUserProfile.setUpdatedAt(LocalDateTime.now());

        mockUpdateRequest = new UpdateProfileRequestDTO();
        mockUpdateRequest.setFirstName("Jane");
        mockUpdateRequest.setLastName("Smith");
        mockUpdateRequest.setPhone("+221-77-987-6543");
        mockUpdateRequest.setAddress("123 Test Street");

        mockChangePasswordRequest = new ChangePasswordRequestDTO();
        mockChangePasswordRequest.setCurrentPassword("oldPassword");
        mockChangePasswordRequest.setNewPassword("newPassword123");
        mockChangePasswordRequest.setConfirmPassword("newPassword123");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getCurrentUserProfile_ShouldReturnUserProfile() throws Exception {
        when(userService.getCurrentUserProfile(anyString())).thenReturn(mockUserProfile);

        mockMvc.perform(get("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateCurrentUserProfile_ShouldReturnUpdatedProfile() throws Exception {
        UserProfileResponseDTO updatedProfile = new UserProfileResponseDTO();
        updatedProfile.setFirstName("Jane");
        updatedProfile.setLastName("Smith");
        
        when(userService.updateCurrentUserProfile(anyString(), any(UpdateProfileRequestDTO.class)))
                .thenReturn(updatedProfile);

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void changePassword_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(post("/api/users/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockChangePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    @WithMockUser(username = "doctor@example.com", roles = {"DOCTOR"})
    void getUserProfileById_ShouldReturnUserProfile() throws Exception {
        when(userService.getUserProfileById(1L)).thenReturn(mockUserProfile);

        mockMvc.perform(get("/api/users/profile/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "patient@example.com", roles = {"PATIENT"})
    void getUserProfileById_AsPatient_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users/profile/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentUserProfile_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
