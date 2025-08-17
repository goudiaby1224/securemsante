package sn.goudiaby.msante.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvailabilityDTO {
    
    private Long id;
    private Long doctorId;
    private String doctorName;
    private String specialty;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}