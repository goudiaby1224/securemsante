package sn.goudiaby.msante.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.Appointment;
import sn.goudiaby.msante.model.Availability;
import sn.goudiaby.msante.repository.DoctorRepository;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.AppointmentRepository;
import sn.goudiaby.msante.repository.AvailabilityRepository;

import java.util.List;

@Service
public class PerformanceOptimizationService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;

    public PerformanceOptimizationService(DoctorRepository doctorRepository,
                                        PatientRepository patientRepository,
                                        AppointmentRepository appointmentRepository,
                                        AvailabilityRepository availabilityRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
    }

    /**
     * Cache doctors by specialty for better performance
     */
    @Cacheable(value = "doctors", key = "#specialty")
    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        // For now, return all doctors - implement specialty filtering later
        return doctorRepository.findAll();
    }

    /**
     * Cache patient profile for better performance
     */
    @Cacheable(value = "patients", key = "#userId")
    public Patient getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId).orElse(null);
    }

    /**
     * Cache upcoming appointments for better performance
     */
    @Cacheable(value = "appointments", key = "'upcoming'")
    public List<Appointment> getUpcomingAppointments() {
        // For now, return all appointments - implement upcoming filtering later
        return appointmentRepository.findAll();
    }

    /**
     * Cache available slots for better performance
     */
    @Cacheable(value = "availabilities", key = "'available'")
    public List<Availability> getAvailableSlots() {
        return availabilityRepository.findAvailableSlotsAfter(java.time.LocalDateTime.now());
    }

    /**
     * Clear cache when data is updated
     */
    @CacheEvict(value = "doctors", allEntries = true)
    public void clearDoctorsCache() {
        // Cache will be cleared automatically
    }

    /**
     * Clear cache when data is updated
     */
    @CacheEvict(value = "patients", allEntries = true)
    public void clearPatientsCache() {
        // Cache will be cleared automatically
    }

    /**
     * Clear cache when data is updated
     */
    @CacheEvict(value = "appointments", allEntries = true)
    public void clearAppointmentsCache() {
        // Cache will be cleared automatically
    }

    /**
     * Clear cache when data is updated
     */
    @CacheEvict(value = "availabilities", allEntries = true)
    public void clearAvailabilitiesCache() {
        // Cache will be cleared automatically
    }
}
