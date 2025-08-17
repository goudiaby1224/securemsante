package sn.msante.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Availability slot entity for practitioner scheduling
 */
@Entity
@Table(name = "availability_slot")
@Data
@EqualsAndHashCode(callSuper = true)
public class AvailabilitySlot extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id", nullable = false)
    private UserAccount practitioner;
    
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;
    
    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;
    
    @Column(name = "mode", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConsultationMode mode;
    
    @Column(name = "price_cfa", nullable = false)
    private Integer priceCfa;
    
    @Column(name = "is_booked", nullable = false)
    private Boolean isBooked = false;
    
    @Column(name = "booking_buffer_minutes")
    private Integer bookingBufferMinutes = 15; // Time before slot becomes unavailable
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "recurrence_rule")
    private String recurrenceRule; // For recurring slots
    
    public enum ConsultationMode {
        IN_PERSON, VIDEO, PHONE
    }
    
    public boolean isAvailable() {
        return !isBooked && startsAt.isAfter(LocalDateTime.now().plusMinutes(bookingBufferMinutes));
    }
    
    public boolean isInPast() {
        return endsAt.isBefore(LocalDateTime.now());
    }
    
    public long getDurationMinutes() {
        return java.time.Duration.between(startsAt, endsAt).toMinutes();
    }
}