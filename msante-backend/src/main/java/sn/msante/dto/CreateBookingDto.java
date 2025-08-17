package sn.msante.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Booking creation request DTO
 */
@Data
public class CreateBookingDto {
    
    @NotBlank(message = "Practitioner ID is required")
    @JsonProperty("practitioner_id")
    private String practitionerId;
    
    @NotBlank(message = "Slot ID is required")
    @JsonProperty("slot_id")
    private String slotId;
    
    @Valid
    @NotNull(message = "Contact information is required")
    private ContactDto contact;
    
    @JsonProperty("consultation_mode")
    private String mode = "in_person"; // in_person, video
    
    @JsonProperty("consultation_reason")
    private String consultationReason;
    
    private String notes;
    
    @Data
    public static class ContactDto {
        @NotBlank(message = "Name is required")
        private String name;
        
        @NotBlank(message = "Phone is required")
        private String phone;
        
        private String email;
    }
}