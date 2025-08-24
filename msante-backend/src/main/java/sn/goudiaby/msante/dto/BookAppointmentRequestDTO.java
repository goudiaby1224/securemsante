package sn.goudiaby.msante.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookAppointmentRequestDTO {
    
    @NotNull
    private Long availabilityId;
    
    private String notes;

    // Manual getters and setters to ensure compilation works
    public Long getAvailabilityId() { return availabilityId; }
    public void setAvailabilityId(Long availabilityId) { this.availabilityId = availabilityId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}