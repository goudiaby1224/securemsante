package sn.msante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.msante.entity.AvailabilitySlot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for AvailabilitySlot entities
 */
@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, UUID> {
    
    @Query("SELECT a FROM AvailabilitySlot a WHERE a.practitioner.id = :practitionerId " +
           "AND a.startsAt >= :startDate AND a.endsAt <= :endDate " +
           "ORDER BY a.startsAt")
    List<AvailabilitySlot> findByPractitionerAndDateRange(
            @Param("practitionerId") UUID practitionerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AvailabilitySlot a WHERE a.tenant.id = :tenantId " +
           "AND a.isBooked = false AND a.startsAt > :now " +
           "ORDER BY a.startsAt")
    List<AvailabilitySlot> findAvailableSlotsByTenant(
            @Param("tenantId") UUID tenantId,
            @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM AvailabilitySlot a WHERE a.practitioner.id = :practitionerId " +
           "AND a.isBooked = false AND a.startsAt > :now " +
           "ORDER BY a.startsAt")
    List<AvailabilitySlot> findAvailableSlotsByPractitioner(
            @Param("practitionerId") UUID practitionerId,
            @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM AvailabilitySlot a " +
           "WHERE a.isBooked = false " +
           "AND a.startsAt >= :startTime " +
           "AND a.endsAt <= :endTime " +
           "AND (:mode IS NULL OR a.mode = :mode) " +
           "AND (:maxPrice IS NULL OR a.priceCfa <= :maxPrice) " +
           "ORDER BY a.startsAt")
    List<AvailabilitySlot> searchAvailableSlots(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("mode") AvailabilitySlot.ConsultationMode mode,
            @Param("maxPrice") Integer maxPrice);
    
    @Query("SELECT COUNT(a) FROM AvailabilitySlot a WHERE a.practitioner.id = :practitionerId " +
           "AND a.startsAt >= :startDate AND a.startsAt < :endDate")
    long countByPractitionerAndDateRange(
            @Param("practitionerId") UUID practitionerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}