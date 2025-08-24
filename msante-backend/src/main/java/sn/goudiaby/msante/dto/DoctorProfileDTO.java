package sn.goudiaby.msante.dto;

import lombok.Data;
import java.util.List;

@Data
public class DoctorProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String licenseNumber;
    private String specialty;
    private String department;
    private String bio;
    private String education;
    private String experience;
    private List<String> certifications;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
    private String profileImage;
    private String consultationFee;
    private String languages;
    private String workingHours;
}
