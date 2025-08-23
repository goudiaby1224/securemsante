package sn.goudiaby.msante.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Tag(name = "Availability", description = "Endpoints for availability management")
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

    //add availability
    @PostMapping("/add")
    public ResponseEntity<AvailabilityDTO> addAvailability(@RequestBody AvailabilityDTO availabilityDTO) {
        AvailabilityDTO createdAvailability = availabilityService.addAvailability(availabilityDTO);
        return ResponseEntity.status(201).body(createdAvailability);
    }
}