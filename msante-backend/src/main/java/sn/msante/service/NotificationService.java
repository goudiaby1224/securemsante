package sn.msante.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Notification service for SMS/WhatsApp (Mock implementation for MVP)
 */
@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    /**
     * Send OTP via SMS or WhatsApp
     * In production, this would integrate with actual SMS/WhatsApp providers
     */
    public void sendOtp(String phoneNumber, String otpCode, String channel) {
        logger.info("Sending OTP {} to {} via {}", otpCode, phoneNumber, channel);
        
        // Mock implementation - in production this would call:
        // - Orange Money API for SMS
        // - WhatsApp Business API 
        // - Local SMS aggregators
        
        String message = String.format(
            "[M-Santé] Votre code de vérification est: %s. Valide pendant 5 minutes. Ne le partagez pas.",
            otpCode
        );
        
        if ("whatsapp".equals(channel)) {
            sendWhatsApp(phoneNumber, message);
        } else {
            sendSms(phoneNumber, message);
        }
    }
    
    /**
     * Send appointment confirmation
     */
    public void sendAppointmentConfirmation(String phoneNumber, String practitionerName, 
                                          String appointmentDate, String bookingCode) {
        String message = String.format(
            "[M-Santé] Votre RDV avec %s le %s est confirmé. Code: %s",
            practitionerName, appointmentDate, bookingCode
        );
        
        sendSms(phoneNumber, message);
        logger.info("Sent appointment confirmation to {}", phoneNumber);
    }
    
    /**
     * Send appointment reminder
     */
    public void sendAppointmentReminder(String phoneNumber, String practitionerName, 
                                      String appointmentDate) {
        String message = String.format(
            "[M-Santé] Rappel: RDV avec %s demain à %s. Modifiez/annulez: msante.sn",
            practitionerName, appointmentDate
        );
        
        sendSms(phoneNumber, message);
        logger.info("Sent appointment reminder to {}", phoneNumber);
    }
    
    private void sendSms(String phoneNumber, String message) {
        // Mock SMS sending
        logger.info("SMS to {}: {}", phoneNumber, message);
        
        // In production, integrate with:
        // - Orange SMS API
        // - Local SMS aggregators
        // - Twilio (if available in Senegal)
    }
    
    private void sendWhatsApp(String phoneNumber, String message) {
        // Mock WhatsApp sending
        logger.info("WhatsApp to {}: {}", phoneNumber, message);
        
        // In production, integrate with:
        // - WhatsApp Business API
        // - WhatsApp Cloud API
        // - Local WhatsApp aggregators
    }
}