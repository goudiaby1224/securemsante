package sn.goudiaby.msante.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodType;
    private String emergencyContact;
    private String emergencyPhone;
    private String address;
    private String city;
    private String country;
    private String insuranceProvider;
    private String insuranceNumber;
    private List<String> allergies;
    private List<String> medicalConditions;
    private List<String> medications;
    private String medicalHistory;
    private String preferences;
    private String profileImage;
    private Boolean isActive;
}
