package sn.goudiaby.msante.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.goudiaby.msante.dto.AppointmentDTO;
import sn.goudiaby.msante.dto.BookAppointmentRequestDTO;
import sn.goudiaby.msante.model.Appointment;
import sn.goudiaby.msante.model.Availability;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.AppointmentRepository;
import sn.goudiaby.msante.repository.AvailabilityRepository;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.UserRepository;

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
    public AppointmentDTO bookAppointment(BookAppointmentRequestDTO request) {
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
        return convertToDTO(savedAppointment);
    }

    public List<AppointmentDTO> getPatientAppointments() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Appointment> appointments = appointmentRepository.findByPatientEmail(currentUserEmail);
        
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getDoctorAppointments() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Appointment> appointments = appointmentRepository.findByDoctorEmail(currentUserEmail);
        
        return appointments.stream()
                .map(this::convertToDTO)
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
}