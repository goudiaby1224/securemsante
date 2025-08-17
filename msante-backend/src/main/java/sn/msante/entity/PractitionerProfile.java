package sn.msante.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Practitioner profile entity for healthcare professionals
 */
@Entity
@Table(name = "practitioner_profile")
@Data
@EqualsAndHashCode(callSuper = true)
public class PractitionerProfile extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "title")
    private String title; // Dr., Prof., etc.
    
    @Column(name = "specialty", nullable = false)
    private String specialty;
    
    @Column(name = "license_number")
    private String licenseNumber;
    
    @Column(name = "clinic_name")
    private String clinicName;
    
    @Column(name = "clinic_address")
    private String clinicAddress;
    
    @Column(name = "bio", columnDefinition = "text")
    private String bio;
    
    @Column(name = "languages_spoken")
    private String languagesSpoken; // Comma-separated: fr,en,wo
    
    @Column(name = "consultation_fee_cfa")
    private Integer consultationFeeCfa;
    
    @Column(name = "video_consultation_enabled")
    private Boolean videoConsultationEnabled = true;
    
    @Column(name = "verification_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Column(name = "verified_at")
    private java.time.LocalDateTime verifiedAt;
    
    @Column(name = "verified_by")
    private java.util.UUID verifiedBy;
    
    @Column(name = "payout_method")
    @Enumerated(EnumType.STRING)
    private PayoutMethod payoutMethod;
    
    @Column(name = "payout_details", columnDefinition = "jsonb")
    private String payoutDetails; // JSON with account details
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    public enum VerificationStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED
    }
    
    public enum PayoutMethod {
        ORANGE_MONEY, WAVE, FREE_MONEY, BANK_TRANSFER
    }
    
    public String getFullName() {
        StringBuilder name = new StringBuilder();
        if (title != null) {
            name.append(title).append(" ");
        }
        name.append(firstName);
        if (lastName != null) {
            name.append(" ").append(lastName);
        }
        return name.toString();
    }
    
    public boolean isVerified() {
        return verificationStatus == VerificationStatus.APPROVED;
    }
}