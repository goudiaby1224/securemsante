package sn.goudiaby.msante.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sn.goudiaby.msante.dto.RegisterRequestDTO;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.DoctorRepository;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDTO patientRequest;
    private RegisterRequestDTO doctorRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        patientRequest = new RegisterRequestDTO();
        patientRequest.setEmail("patient@example.com");
        patientRequest.setPassword("password123");
        patientRequest.setRole("PATIENT");
        patientRequest.setAddress("123 Main St");
        patientRequest.setPhone("555-1234");
        patientRequest.setBirthDate("1990-01-01");

        doctorRequest = new RegisterRequestDTO();
        doctorRequest.setEmail("doctor@example.com");
        doctorRequest.setPassword("password123");
        doctorRequest.setRole("DOCTOR");
        doctorRequest.setSpecialty("Cardiology");
        doctorRequest.setLicenseNumber("LIC12345");
        doctorRequest.setPhone("555-5678");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setRole(User.Role.PATIENT);
        mockUser.setEnabled(true);
    }

    @Test
    void testRegisterPatientSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());

        User result = userService.registerUser(patientRequest);

        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(patientRequest.getEmail());
        verify(passwordEncoder).encode(patientRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(patientRepository).save(any(Patient.class));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testRegisterDoctorSuccess() {
        mockUser.setRole(User.Role.DOCTOR);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(new Doctor());

        User result = userService.registerUser(doctorRequest);

        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(doctorRequest.getEmail());
        verify(passwordEncoder).encode(doctorRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(doctorRepository).save(any(Doctor.class));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testRegisterUserEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(patientRequest);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(patientRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(patientRepository, never()).save(any(Patient.class));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testRegisterPatientWithNullBirthDate() {
        patientRequest.setBirthDate(null);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());

        User result = userService.registerUser(patientRequest);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testRegisterPatientWithEmptyBirthDate() {
        patientRequest.setBirthDate("");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());

        User result = userService.registerUser(patientRequest);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testFindByEmailSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        User result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindByEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findByEmail("nonexistent@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testRegisterPatientWithInvalidDateFormat() {
        patientRequest.setBirthDate("invalid-date");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        assertThrows(Exception.class, () -> {
            userService.registerUser(patientRequest);
        });
    }

    @Test
    void testCreatePatientProfileWithValidData() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            assertEquals("123 Main St", patient.getAddress());
            assertEquals("555-1234", patient.getPhone());
            assertEquals(LocalDate.of(1990, 1, 1), patient.getBirthDate());
            return patient;
        });

        userService.registerUser(patientRequest);

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testCreateDoctorProfileWithValidData() {
        mockUser.setRole(User.Role.DOCTOR);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor doctor = invocation.getArgument(0);
            assertEquals("Cardiology", doctor.getSpecialty());
            assertEquals("LIC12345", doctor.getLicenseNumber());
            assertEquals("555-5678", doctor.getPhone());
            return doctor;
        });

        userService.registerUser(doctorRequest);

        verify(doctorRepository).save(any(Doctor.class));
    }
}