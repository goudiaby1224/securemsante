package sn.goudiaby.msante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import sn.goudiaby.msante.config.SecurityConfig;
import sn.goudiaby.msante.dto.AppointmentResponseDTO;
import sn.goudiaby.msante.dto.BookAppointmentRequestDTO;
import sn.goudiaby.msante.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookAppointmentRequestDTO bookRequest;
    private AppointmentResponseDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        bookRequest = new BookAppointmentRequestDTO();
        bookRequest.setAvailabilityId(1L);
        bookRequest.setNotes("Regular checkup");

        appointmentDTO = new AppointmentResponseDTO();
        appointmentDTO.setId(1L);
        appointmentDTO.setPatientId(1L);
        appointmentDTO.setPatientName("John Doe");
        appointmentDTO.setDoctorId(1L);
        appointmentDTO.setDoctorName("Dr. Smith");
        appointmentDTO.setSpecialty("Cardiology");
        appointmentDTO.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointmentDTO.setStatus("CONFIRMED");
        appointmentDTO.setNotes("Regular checkup");
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testBookAppointmentSuccess() throws Exception {
        when(appointmentService.bookAppointment(any(BookAppointmentRequestDTO.class)))
                .thenReturn(appointmentDTO);

        mockMvc.perform(post("/api/appointments/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientName").value("John Doe"))
                .andExpect(jsonPath("$.doctorName").value("Dr. Smith"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(appointmentService).bookAppointment(any(BookAppointmentRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testBookAppointmentFailure() throws Exception {
        when(appointmentService.bookAppointment(any(BookAppointmentRequestDTO.class)))
                .thenThrow(new RuntimeException("Availability not found"));

        mockMvc.perform(post("/api/appointments/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testBookAppointmentAccessDenied() throws Exception {
        mockMvc.perform(post("/api/appointments/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testBookAppointmentUnauthorized() throws Exception {
        mockMvc.perform(post("/api/appointments/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testGetPatientAppointments() throws Exception {
        List<AppointmentResponseDTO> appointments = Arrays.asList(appointmentDTO);
        when(appointmentService.getPatientAppointments()).thenReturn(appointments);

        mockMvc.perform(get("/api/appointments/patient")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].patientName").value("John Doe"));

        verify(appointmentService).getPatientAppointments();
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testGetDoctorAppointments() throws Exception {
        List<AppointmentResponseDTO> appointments = Arrays.asList(appointmentDTO);
        when(appointmentService.getDoctorAppointments()).thenReturn(appointments);

        mockMvc.perform(get("/api/appointments/doctor")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].doctorName").value("Dr. Smith"));

        verify(appointmentService).getDoctorAppointments();
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testGetDoctorAppointmentsAccessDenied() throws Exception {
        mockMvc.perform(get("/api/appointments/doctor")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testCancelAppointment() throws Exception {
        doNothing().when(appointmentService).cancelAppointment(anyLong());

        mockMvc.perform(delete("/api/appointments/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Appointment cancelled successfully"));

        verify(appointmentService).cancelAppointment(1L);
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testCancelAppointmentNotFound() throws Exception {
        doThrow(new RuntimeException("Appointment not found"))
                .when(appointmentService).cancelAppointment(anyLong());

        mockMvc.perform(delete("/api/appointments/999")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to cancel appointment: Appointment not found"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testBookAppointmentInvalidInput() throws Exception {
        bookRequest.setAvailabilityId(null);

        mockMvc.perform(post("/api/appointments/book")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isBadRequest());
    }
}