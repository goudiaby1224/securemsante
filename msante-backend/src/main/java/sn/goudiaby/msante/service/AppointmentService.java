package sn.goudiaby.msante.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.goudiaby.msante.dto.*;
import sn.goudiaby.msante.model.Appointment;
import sn.goudiaby.msante.model.Availability;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.AppointmentRepository;
import sn.goudiaby.msante.repository.AvailabilityRepository;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public AppointmentResponseDTO bookAppointment(BookAppointmentRequestDTO request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        Availability availability = availabilityRepository.findById(request.getAvailabilityId())
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        if (availability.getStatus() != Availability.Status.AVAILABLE) {
            throw new RuntimeException("Availability slot is no longer available");
        }

        // Mark availability as booked
        availability.setStatus(Availability.Status.BOOKED);
        availabilityRepository.save(availability);

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(availability.getDoctor());
        appointment.setAvailability(availability);
        appointment.setStatus(Appointment.Status.CONFIRMED);
        appointment.setNotes(request.getNotes());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromAppointment(savedAppointment);
    }

    public List<AppointmentResponseDTO> getPatientAppointments() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Appointment> appointments = appointmentRepository.findByPatientEmail(currentUserEmail);
        
        return appointments.stream()
                .map(AppointmentResponseDTO::fromAppointment)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDTO> getDoctorAppointments() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Appointment> appointments = appointmentRepository.findByDoctorEmail(currentUserEmail);
        
        return appointments.stream()
                .map(AppointmentResponseDTO::fromAppointment)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify user has permission to cancel
        boolean canCancel = appointment.getPatient().getUser().getEmail().equals(currentUserEmail) ||
                           appointment.getDoctor().getUser().getEmail().equals(currentUserEmail);

        if (!canCancel) {
            throw new RuntimeException("Not authorized to cancel this appointment");
        }

        // Cancel appointment and make availability slot available again
        appointment.setStatus(Appointment.Status.CANCELLED);
        appointment.getAvailability().setStatus(Availability.Status.AVAILABLE);
        
        appointmentRepository.save(appointment);
        availabilityRepository.save(appointment.getAvailability());
    }

    public List<AppointmentResponseDTO> searchAvailableSlots(SearchAvailabilityRequestDTO request) {
        LocalDateTime startOfDay = request.getDate().atStartOfDay();
        LocalDateTime endOfDay = request.getDate().atTime(LocalTime.MAX);
        
        List<Availability> availabilities = availabilityRepository.findAvailableSlots(
            startOfDay, 
            endOfDay, 
            request.getSpecialty(), 
            request.getDoctorId()
        );
        
        return availabilities.stream()
            .map(this::convertAvailabilityToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponseDTO rescheduleAppointment(Long appointmentId, Long newAvailabilityId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // Verify user has permission to reschedule
        boolean canReschedule = appointment.getPatient().getUser().getEmail().equals(currentUserEmail) ||
                               appointment.getDoctor().getUser().getEmail().equals(currentUserEmail);
        
        if (!canReschedule) {
            throw new RuntimeException("Not authorized to reschedule this appointment");
        }
        
        // Get new availability slot
        Availability newAvailability = availabilityRepository.findById(newAvailabilityId)
            .orElseThrow(() -> new RuntimeException("New availability slot not found"));
        
        if (newAvailability.getStatus() != Availability.Status.AVAILABLE) {
            throw new RuntimeException("New availability slot is not available");
        }
        
        // Free up old availability slot
        appointment.getAvailability().setStatus(Availability.Status.AVAILABLE);
        availabilityRepository.save(appointment.getAvailability());
        
        // Book new availability slot
        newAvailability.setStatus(Availability.Status.BOOKED);
        availabilityRepository.save(newAvailability);
        
        // Update appointment
        appointment.setAvailability(newAvailability);
        appointment.setStatus(Appointment.Status.RESCHEDULED);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromAppointment(savedAppointment);
    }

    public List<AppointmentResponseDTO> getUpcomingAppointments() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Appointment> appointments = appointmentRepository.findUpcomingAppointments(email, LocalDateTime.now());
            return appointments.stream()
                    .map(this::convertAvailabilityToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving upcoming appointments: " + e.getMessage());
        }
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getUser().getEmail()); // Could be improved
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getUser().getEmail()); // Could be improved
        dto.setSpecialty(appointment.getDoctor().getSpecialty());
        dto.setAppointmentTime(appointment.getAvailability().getStartTime());
        dto.setStatus(appointment.getStatus().name());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    private AppointmentResponseDTO convertAvailabilityToDTO(Availability availability) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setDoctorId(availability.getDoctor().getId());
        dto.setDoctorFirstName(availability.getDoctor().getUser().getFirstName());
        dto.setDoctorLastName(availability.getDoctor().getUser().getLastName());
        dto.setDoctorEmail(availability.getDoctor().getUser().getEmail());
        dto.setDoctorSpecialty(availability.getDoctor().getSpecialty());
        dto.setDoctorDepartment(availability.getDoctor().getLicenseNumber());
        dto.setAppointmentTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setStatus("AVAILABLE");
        return dto;
    }
}