package sn.msante.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Patient profile entity
 */
@Entity
@Table(name = "patient_profile")
@Data
@EqualsAndHashCode(callSuper = true)
public class PatientProfile extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;
    
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "medical_history", columnDefinition = "text")
    private String medicalHistory;
    
    @Column(name = "allergies", columnDefinition = "text")
    private String allergies;
    
    @Column(name = "current_medications", columnDefinition = "text")
    private String currentMedications;
    
    @Column(name = "privacy_consent", nullable = false)
    private Boolean privacyConsent = false;
    
    @Column(name = "marketing_consent")
    private Boolean marketingConsent = false;
    
    public enum Gender {
        MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : lastName;
    }
}