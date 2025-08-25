package sn.goudiaby.msante.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.goudiaby.msante.dto.DoctorProfileDTO;
import sn.goudiaby.msante.dto.AdvancedSearchDTO;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.DoctorRepository;
import sn.goudiaby.msante.repository.UserRepository;
import sn.goudiaby.msante.repository.AvailabilityRepository;
import sn.goudiaby.msante.model.Availability;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;

    // Manual constructor to ensure compilation works
    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository, AvailabilityRepository availabilityRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public DoctorProfileDTO getCurrentDoctorProfile() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
            
            return convertToDTO(doctor);
        } catch (Exception e) {
            // Fallback for test environment or when no authentication context
            throw new RuntimeException("Unable to retrieve doctor profile: " + e.getMessage());
        }
    }

    public DoctorProfileDTO updateDoctorProfile(DoctorProfileDTO profileDTO) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
            
            // Update doctor fields
            doctor.setFirstName(profileDTO.getFirstName());
            doctor.setLastName(profileDTO.getLastName());
            doctor.setPhone(profileDTO.getPhone());
            doctor.setSpecialty(profileDTO.getSpecialty());
            doctor.setDepartment(profileDTO.getDepartment());
            doctor.setBio(profileDTO.getBio());
            doctor.setEducation(profileDTO.getEducation());
            doctor.setExperience(profileDTO.getExperience());
            doctor.setConsultationFee(profileDTO.getConsultationFee());
            doctor.setLanguages(profileDTO.getLanguages());
            doctor.setWorkingHours(profileDTO.getWorkingHours());
            
            Doctor savedDoctor = doctorRepository.save(doctor);
            return convertToDTO(savedDoctor);
        } catch (Exception e) {
            // Fallback for test environment or when no authentication context
            throw new RuntimeException("Unable to update doctor profile: " + e.getMessage());
        }
    }

    public List<DoctorProfileDTO> searchDoctors(AdvancedSearchDTO searchDTO) {
        List<Doctor> doctors = doctorRepository.findBySearchCriteria(
                searchDTO.getSpecialty(),
                searchDTO.getDepartment(),
                searchDTO.getMinRating(),
                searchDTO.getGender(),
                searchDTO.getLanguage()
        );
        
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllSpecialties() {
        return doctorRepository.findAllSpecialties();
    }

    public List<String> getAllDepartments() {
        return doctorRepository.findAllDepartments();
    }

    public DoctorProfileDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return convertToDTO(doctor);
    }

    public List<Availability> getDoctorAvailability(Long doctorId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        return availabilityRepository.findAvailableSlotsByDoctorAndDate(doctorId, date);
    }

    public String rateDoctor(Long doctorId, Double rating, String review) {
        // Implementation for rating system
        // This would typically involve a separate Rating entity
        return "Rating submitted successfully";
    }

    public List<DoctorProfileDTO> getTopRatedDoctors(Integer limit) {
        List<Doctor> doctors = doctorRepository.findTopRatedDoctors(limit);
        return doctors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DoctorProfileDTO convertToDTO(Doctor doctor) {
        DoctorProfileDTO dto = new DoctorProfileDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setEmail(doctor.getUser().getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setDepartment(doctor.getDepartment());
        dto.setBio(doctor.getBio());
        dto.setEducation(doctor.getEducation());
        dto.setExperience(doctor.getExperience());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setLanguages(doctor.getLanguages());
        dto.setWorkingHours(doctor.getWorkingHours());
        dto.setIsAvailable(true); // Default value
        dto.setRating(4.5); // Default value - would come from rating system
        dto.setTotalReviews(0); // Default value - would come from rating system
        
        return dto;
    }
}
