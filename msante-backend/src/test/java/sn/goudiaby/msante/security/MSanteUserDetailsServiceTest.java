package sn.goudiaby.msante.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MSanteUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MSanteUserDetailsService userDetailsService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole(User.Role.PATIENT);
        mockUser.setEnabled(true);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PATIENT")));

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        assertEquals("User not found: nonexistent@example.com", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testLoadUserByUsernameDoctorRole() {
        mockUser.setRole(User.Role.DOCTOR);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername("doctor@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_DOCTOR")));

        verify(userRepository).findByEmail("doctor@example.com");
    }

    @Test
    void testLoadUserByUsernameDisabledUser() {
        mockUser.setEnabled(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertFalse(result.isEnabled());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testUserDetailsImplementation() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        // Test UserDetails interface methods
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
    }
}