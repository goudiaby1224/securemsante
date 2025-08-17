package sn.msante.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Booking entity for appointment reservations
 */
@Entity
@Table(name = "booking")
@Data
@EqualsAndHashCode(callSuper = true)
public class Booking extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private UserAccount patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id", nullable = false)
    private UserAccount practitioner;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private AvailabilitySlot slot;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;
    
    @Column(name = "patient_name")
    private String patientName; // For guest bookings
    
    @Column(name = "patient_phone")
    private String patientPhone; // For guest bookings
    
    @Column(name = "patient_email")
    private String patientEmail; // For guest bookings
    
    @Column(name = "consultation_reason")
    private String consultationReason;
    
    @Column(name = "notes", columnDefinition = "text")
    private String notes;
    
    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;
    
    @Column(name = "confirmed_at")
    private java.time.LocalDateTime confirmedAt;
    
    @Column(name = "cancelled_at")
    private java.time.LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "cancelled_by")
    private java.util.UUID cancelledBy;
    
    @Column(name = "completed_at")
    private java.time.LocalDateTime completedAt;
    
    @Column(name = "no_show_at")
    private java.time.LocalDateTime noShowAt;
    
    public enum BookingStatus {
        PENDING_PAYMENT, CONFIRMED, CANCELLED, REFUNDED, NO_SHOW, COMPLETED
    }
    
    public boolean isGuest() {
        return patient == null && patientPhone != null;
    }
    
    public String getPatientDisplayName() {
        if (isGuest()) {
            return patientName != null ? patientName : patientPhone;
        }
        return patient != null ? patient.getEmail() : "Unknown";
    }
    
    public boolean canBeCancelled() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.PENDING_PAYMENT;
    }
    
    public boolean canBeCompleted() {
        return status == BookingStatus.CONFIRMED && 
               slot.getStartsAt().isBefore(java.time.LocalDateTime.now());
    }
}