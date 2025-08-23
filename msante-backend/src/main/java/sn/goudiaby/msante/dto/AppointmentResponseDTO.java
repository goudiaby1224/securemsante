package sn.goudiaby.msante.dto;

import lombok.Data;
import sn.goudiaby.msante.model.Appointment;

import java.time.LocalDateTime;

@Data
public class AppointmentResponseDTO {
    
    private Long id;
    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientEmail;
    private String patientPhone;
    
    private Long doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorEmail;
    private String doctorSpecialty;
    private String doctorDepartment;
    
    private LocalDateTime appointmentTime;
    private LocalDateTime endTime;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static AppointmentResponseDTO fromAppointment(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        
        // Patient information
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientFirstName(appointment.getPatient().getUser().getFirstName());
        dto.setPatientLastName(appointment.getPatient().getUser().getLastName());
        dto.setPatientEmail(appointment.getPatient().getUser().getEmail());
        dto.setPatientPhone(appointment.getPatient().getUser().getPhone());
        
        // Doctor information
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorFirstName(appointment.getDoctor().getUser().getFirstName());
        dto.setDoctorLastName(appointment.getDoctor().getUser().getLastName());
        dto.setDoctorEmail(appointment.getDoctor().getUser().getEmail());
        dto.setDoctorSpecialty(appointment.getDoctor().getSpecialty());
        dto.setDoctorDepartment(appointment.getDoctor().getLicenseNumber());
        
        // Appointment details
        dto.setAppointmentTime(appointment.getAvailability().getStartTime());
        dto.setEndTime(appointment.getAvailability().getEndTime());
        dto.setStatus(appointment.getStatus().name());
        dto.setNotes(appointment.getNotes());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        
        return dto;
    }
}
