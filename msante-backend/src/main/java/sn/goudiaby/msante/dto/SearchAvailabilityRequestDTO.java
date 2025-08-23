package sn.goudiaby.msante.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SearchAvailabilityRequestDTO {
    
    @NotNull(message = "Date is required")
    @Future(message = "Date must be in the future")
    private LocalDate date;
    
    private String specialty;
    private Long doctorId;
    private List<String> preferredTimes; // e.g., ["09:00", "14:00"]
    private Integer maxDistance; // in kilometers, for future location-based search
}
