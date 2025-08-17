package sn.goudiaby.msante.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.goudiaby.msante.dto.AvailabilityDTO;
import sn.goudiaby.msante.service.AvailabilityService;

import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://localhost:4200"})
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<AvailabilityDTO>> getAllAvailableSlots() {
        List<AvailabilityDTO> availabilities = availabilityService.getAvailableSlots();
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailableSlotsByDoctor(@PathVariable Long doctorId) {
        List<AvailabilityDTO> availabilities = availabilityService.getAvailableSlotsByDoctor(doctorId);
        return ResponseEntity.ok(availabilities);
    }
}