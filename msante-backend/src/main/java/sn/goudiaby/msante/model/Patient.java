package sn.goudiaby.msante.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(length = 50)
    private String phone;

    @Column
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(length = 10)
    private String bloodType;

    @Column(length = 100)
    private String emergencyContact;

    @Column(length = 50)
    private String emergencyPhone;

    @Column
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String insuranceProvider;

    @Column(length = 100)
    private String insuranceNumber;

    @ElementCollection
    @CollectionTable(name = "patient_allergies", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "allergy")
    private List<String> allergies;

    @ElementCollection
    @CollectionTable(name = "patient_medical_conditions", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "condition")
    private List<String> medicalConditions;

    @ElementCollection
    @CollectionTable(name = "patient_medications", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "medication")
    private List<String> medications;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(columnDefinition = "TEXT")
    private String preferences;

    @Column(length = 255)
    private String profileImage;

    @Column
    private Boolean isActive = true;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Appointment> appointments;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Manual getters and setters to ensure compilation works
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getInsuranceProvider() { return insuranceProvider; }
    public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }
    
    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }
    
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    
    public List<String> getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(List<String> medicalConditions) { this.medicalConditions = medicalConditions; }
    
    public List<String> getMedications() { return medications; }
    public void setMedications(List<String> medications) { this.medications = medications; }
    
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
    
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}