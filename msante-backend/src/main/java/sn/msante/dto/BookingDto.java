package sn.msante.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Booking response DTO
 */
@Data
public class BookingDto {
    
    @JsonProperty("booking_id")
    private String bookingId;
    
    @JsonProperty("practitioner_id")
    private String practitionerId;
    
    @JsonProperty("practitioner_name")
    private String practitionerName;
    
    @JsonProperty("slot_id")
    private String slotId;
    
    @JsonProperty("starts_at")
    private LocalDateTime startsAt;
    
    @JsonProperty("ends_at")
    private LocalDateTime endsAt;
    
    @JsonProperty("consultation_mode")
    private String consultationMode;
    
    @JsonProperty("price_cfa")
    private Integer priceCfa;
    
    private String status;
    
    @JsonProperty("patient_name")
    private String patientName;
    
    @JsonProperty("consultation_reason")
    private String consultationReason;
    
    private String notes;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("confirmed_at")
    private LocalDateTime confirmedAt;
    
    @JsonProperty("payment_status")
    private String paymentStatus;
    
    @JsonProperty("can_be_cancelled")
    private Boolean canBeCancelled;
    
    @JsonProperty("cancellation_deadline")
    private LocalDateTime cancellationDeadline;
}