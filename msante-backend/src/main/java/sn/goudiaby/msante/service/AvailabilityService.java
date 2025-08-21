package sn.goudiaby.msante.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.goudiaby.msante.dto.AvailabilityDTO;
import sn.goudiaby.msante.model.Availability;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.repository.AvailabilityRepository;
import sn.goudiaby.msante.repository.DoctorRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    public List<AvailabilityDTO> getAvailableSlots() {
        LocalDateTime now = LocalDateTime.now();
        List<Availability> availabilities = availabilityRepository.findAvailableSlots(now);
        
        return availabilities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AvailabilityDTO> getAvailableSlotsByDoctor(Long doctorId) {
        List<Availability> availabilities = availabilityRepository
                .findByDoctorIdAndStatus(doctorId, Availability.Status.AVAILABLE);
        
        return availabilities.stream()
                .filter(a -> a.getStartTime().isAfter(LocalDateTime.now()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Availability createAvailability(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Availability availability = new Availability();
        availability.setDoctor(doctor);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setStatus(Availability.Status.AVAILABLE);

        return availabilityRepository.save(availability);
    }

    private AvailabilityDTO convertToDTO(Availability availability) {
        AvailabilityDTO dto = new AvailabilityDTO();
        dto.setId(availability.getId());
        dto.setDoctorId(availability.getDoctor().getId());
        dto.setDoctorName(availability.getDoctor().getUser().getEmail()); // Could be improved with actual name
        dto.setSpecialty(availability.getDoctor().getSpecialty());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setStatus(availability.getStatus().name());
        return dto;
    }

    public AvailabilityDTO addAvailability(AvailabilityDTO availabilityDTO) {
    Doctor doctor = doctorRepository.findById(availabilityDTO.getDoctorId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
    Availability availability = new Availability();
    availability.setDoctor(doctor);
    availability.setStartTime(availabilityDTO.getStartTime());
    availability.setEndTime(availabilityDTO.getEndTime());
    availability.setStatus(Availability.Status.AVAILABLE);
    availability.setCreatedAt(LocalDateTime.now());
    Availability savedAvailability = availabilityRepository.save(availability);
    if (savedAvailability != null) {
        return convertToDTO(savedAvailability);

    }
        return null;
    }
}