package sn.goudiaby.msante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.goudiaby.msante.model.Availability;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    
    List<Availability> findByDoctorIdAndStatus(Long doctorId, Availability.Status status);
    
    @Query("SELECT a FROM Availability a WHERE a.doctor.id = :doctorId " +
           "AND a.status = :status " +
           "AND a.startTime BETWEEN :startDate AND :endDate")
    List<Availability> findByDoctorAndStatusAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("status") Availability.Status status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Availability a WHERE a.status = 'AVAILABLE' " +
           "AND a.startTime > :now " +
           "ORDER BY a.startTime ASC")
    List<Availability> findAvailableSlots(@Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Availability a WHERE a.status = 'AVAILABLE' " +
           "AND a.startTime BETWEEN :startTime AND :endTime " +
           "AND (:specialty IS NULL OR a.doctor.specialty = :specialty) " +
           "AND (:doctorId IS NULL OR a.doctor.id = :doctorId) " +
           "ORDER BY a.startTime ASC")
    List<Availability> findAvailableSlots(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("specialty") String specialty,
            @Param("doctorId") Long doctorId);
}