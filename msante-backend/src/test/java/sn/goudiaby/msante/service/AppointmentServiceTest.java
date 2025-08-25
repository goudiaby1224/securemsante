package sn.goudiaby.msante.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import sn.goudiaby.msante.dto.AppointmentResponseDTO;
import sn.goudiaby.msante.dto.BookAppointmentRequestDTO;
import sn.goudiaby.msante.model.*;
import sn.goudiaby.msante.repository.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private User patientUser;
    private Patient patient;
    private Doctor doctor;
    private Availability availability;
    private BookAppointmentRequestDTO bookRequest;

    @BeforeEach
    void setUp() {
        patientUser = new User();
        patientUser.setId(1L);
        patientUser.setEmail("patient@example.com");
        patientUser.setRole(User.Role.PATIENT);

        patient = new Patient();
        patient.setId(1L);
        patient.setUser(patientUser);
        patient.setAddress("123 Test St");

        User doctorUser = new User();
        doctorUser.setId(2L);
        doctorUser.setEmail("doctor@example.com");
        doctorUser.setRole(User.Role.DOCTOR);

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUser(doctorUser);
        doctor.setSpecialty("Cardiology");
        doctor.setLicenseNumber("LIC123");

        availability = new Availability();
        availability.setId(1L);
        availability.setDoctor(doctor);
        availability.setStartTime(LocalDateTime.now().plusDays(1));
        availability.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        availability.setStatus(Availability.Status.AVAILABLE);

        bookRequest = new BookAppointmentRequestDTO();
        bookRequest.setAvailabilityId(1L);
        bookRequest.setNotes("Test appointment");
    }

    private Authentication createMockAuthentication() {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return patientUser;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                // Mock implementation
            }

            @Override
            public String getName() {
                return "patient@example.com";
            }
        };
    }

    @Test
    void testBookAppointmentSuccess() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));
        when(availabilityRepository.save(any(Availability.class))).thenReturn(availability);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAvailability(availability);
        appointment.setStatus(Appointment.Status.CONFIRMED);
        appointment.setNotes("Test appointment");

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponseDTO result = appointmentService.bookAppointment(bookRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test appointment", result.getNotes());
        verify(availabilityRepository).save(any(Availability.class));
        verify(appointmentRepository).save(any(Appointment.class));

        // Verify availability status was changed to BOOKED
        assertEquals(Availability.Status.BOOKED, availability.getStatus());
    }

    @Test
    void testBookAppointmentUserNotFound() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(bookRequest);
        });

        assertEquals("User not found with email: patient@example.com", exception.getMessage());
        verify(userRepository).findByEmail("patient@example.com");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testBookAppointmentPatientProfileNotFound() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(bookRequest);
        });

        assertEquals("Patient profile not found", exception.getMessage());
        verify(patientRepository).findByUserId(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testBookAppointmentAvailabilityNotFound() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(availabilityRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(bookRequest);
        });

        assertEquals("Availability slot not found", exception.getMessage());
        verify(availabilityRepository).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testBookAppointmentSlotNotAvailable() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());

        availability.setStatus(Availability.Status.BOOKED);

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(availability));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(bookRequest);
        });

        assertEquals("Availability slot is no longer available", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testGetPatientAppointments() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());

        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAvailability(availability);
        appointment.setStatus(Appointment.Status.CONFIRMED);

        when(appointmentRepository.findByPatientId(1L))
                .thenReturn(Arrays.asList(appointment));

        List<AppointmentResponseDTO> result = appointmentService.getPatientAppointments();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByPatientId(1L);
    }

    @Test
    void testCancelAppointment() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());
        
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAvailability(availability);
        appointment.setStatus(Appointment.Status.CONFIRMED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(availabilityRepository.save(any(Availability.class))).thenReturn(availability);

        appointmentService.cancelAppointment(1L);

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(availabilityRepository).save(any(Availability.class));

        // Verify appointment status was changed to CANCELLED
        assertEquals(Appointment.Status.CANCELLED, appointment.getStatus());
        // Verify availability status was changed back to AVAILABLE
        assertEquals(Availability.Status.AVAILABLE, availability.getStatus());
    }

    @Test
    void testCancelAppointmentNotFound() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createMockAuthentication());

        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.cancelAppointment(999L);
        });

        assertEquals("Appointment not found", exception.getMessage());
        verify(appointmentRepository).findById(999L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}