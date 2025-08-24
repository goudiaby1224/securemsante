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
    private String patientName; // Convenience field for full name
    
    private Long doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorEmail;
    private String doctorSpecialty;
    private String doctorDepartment;
    private String doctorName; // Convenience field for full name
    private String specialty; // General specialty field for the appointment
    
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
        dto.setPatientName(appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName());
        
        // Doctor information
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorFirstName(appointment.getDoctor().getUser().getFirstName());
        dto.setDoctorLastName(appointment.getDoctor().getUser().getLastName());
        dto.setDoctorEmail(appointment.getDoctor().getUser().getEmail());
        dto.setDoctorSpecialty(appointment.getDoctor().getSpecialty());
        dto.setDoctorDepartment(appointment.getDoctor().getLicenseNumber());
        dto.setDoctorName(appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName());
        
        // Appointment details
        dto.setAppointmentTime(appointment.getAvailability().getStartTime());
        dto.setEndTime(appointment.getAvailability().getEndTime());
        dto.setStatus(appointment.getStatus().name());
        dto.setNotes(appointment.getNotes());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        
        return dto;
    }
    
    // Manual getters and setters to ensure compilation works
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    
    public String getPatientFirstName() { return patientFirstName; }
    public void setPatientFirstName(String patientFirstName) { this.patientFirstName = patientFirstName; }
    
    public String getPatientLastName() { return patientLastName; }
    public void setPatientLastName(String patientLastName) { this.patientLastName = patientLastName; }
    
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
    
    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }
    
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    
    public String getDoctorFirstName() { return doctorFirstName; }
    public void setDoctorFirstName(String doctorFirstName) { this.doctorFirstName = doctorFirstName; }
    
    public String getDoctorLastName() { return doctorLastName; }
    public void setDoctorLastName(String doctorLastName) { this.doctorLastName = doctorLastName; }
    
    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }
    
    public String getDoctorSpecialty() { return doctorSpecialty; }
    public void setDoctorSpecialty(String doctorSpecialty) { this.doctorSpecialty = doctorSpecialty; }
    
    public String getDoctorDepartment() { return doctorDepartment; }
    public void setDoctorDepartment(String doctorDepartment) { this.doctorDepartment = doctorDepartment; }
    
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.appointmentTime = appointmentTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
