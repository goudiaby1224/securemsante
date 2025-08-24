package sn.goudiaby.msante.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AdvancedSearchDTO {
    private String specialty;
    private String department;
    private Long doctorId;
    private LocalDate date;
    private LocalTime preferredStartTime;
    private LocalTime preferredEndTime;
    private String location;
    private Double maxDistance;
    private Double minRating;
    private String gender;
    private String language;
    private String insuranceProvider;
    private Double maxConsultationFee;
    private Boolean isEmergency;
    private List<String> certifications;
    private String availability;
    private String sortBy;
    private String sortOrder;
    private Integer page;
    private Integer size;
}
