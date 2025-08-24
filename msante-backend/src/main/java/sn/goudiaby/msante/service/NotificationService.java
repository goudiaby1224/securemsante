package sn.goudiaby.msante.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sn.goudiaby.msante.model.Appointment;
import sn.goudiaby.msante.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final AppointmentService appointmentService;

    public void sendAppointmentConfirmation(Appointment appointment) {
        // Send confirmation email/SMS to patient
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorName = appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName();
        String appointmentTime = appointment.getAvailability().getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        log.info("Sending appointment confirmation to {} for appointment with Dr. {} at {}", 
                patientEmail, doctorName, appointmentTime);
        
        // TODO: Integrate with email/SMS service
        // emailService.sendAppointmentConfirmation(patientEmail, appointment);
    }

    public void sendAppointmentReminder(Appointment appointment) {
        // Send reminder 24 hours before appointment
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorName = appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName();
        String appointmentTime = appointment.getAvailability().getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        log.info("Sending appointment reminder to {} for appointment with Dr. {} at {}", 
                patientEmail, doctorName, appointmentTime);
        
        // TODO: Integrate with email/SMS service
        // emailService.sendAppointmentReminder(patientEmail, appointment);
    }

    public void sendAppointmentCancellation(Appointment appointment, String reason) {
        // Send cancellation notification to patient
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorName = appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName();
        
        log.info("Sending appointment cancellation to {} for appointment with Dr. {}. Reason: {}", 
                patientEmail, doctorName, reason);
        
        // TODO: Integrate with email/SMS service
        // emailService.sendAppointmentCancellation(patientEmail, appointment, reason);
    }

    public void sendAppointmentReschedule(Appointment appointment, LocalDateTime newTime) {
        // Send reschedule notification to patient
        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorName = appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName();
        String newAppointmentTime = newTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        log.info("Sending appointment reschedule to {} for appointment with Dr. {}. New time: {}", 
                patientEmail, doctorName, newAppointmentTime);
        
        // TODO: Integrate with email/SMS service
        // emailService.sendAppointmentReschedule(patientEmail, appointment, newTime);
    }

    public void sendDoctorAvailabilityUpdate(User doctor, String message) {
        // Send notification to patients about doctor availability changes
        log.info("Sending availability update notification for Dr. {}: {}", 
                doctor.getEmail(), message);
        
        // TODO: Integrate with email/SMS service
        // emailService.sendAvailabilityUpdate(doctor, message);
    }

    public void sendEmergencyNotification(User patient, String message) {
        // Send emergency notification to patient
        log.info("Sending emergency notification to {}: {}", 
                patient.getEmail(), message);
        
        // TODO: Integrate with email/SMS service
        // emailService.sendEmergencyNotification(patient, message);
    }

    public void scheduleReminders() {
        // Schedule reminders for upcoming appointments
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDate tomorrowDate = tomorrow.toLocalDate();
        List<Appointment> tomorrowAppointments = appointmentService.getAppointmentsByDate(tomorrowDate);
        
        for (Appointment appointment : tomorrowAppointments) {
            sendAppointmentReminder(appointment);
        }
    }
}
