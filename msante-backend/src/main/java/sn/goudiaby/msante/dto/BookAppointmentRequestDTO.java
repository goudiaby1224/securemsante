package sn.goudiaby.msante.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookAppointmentRequestDTO {
    
    @NotNull
    private Long availabilityId;
    
    private String notes;
}