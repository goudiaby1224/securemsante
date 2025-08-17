package sn.msante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.msante.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Booking entities
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    List<Booking> findByPatientId(UUID patientId);
    
    List<Booking> findByPractitionerId(UUID practitionerId);
    
    @Query("SELECT b FROM Booking b WHERE b.tenant.id = :tenantId " +
           "AND b.status = :status ORDER BY b.createdAt DESC")
    List<Booking> findByTenantAndStatus(
            @Param("tenantId") UUID tenantId,
            @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.patient.id = :patientId " +
           "AND b.status IN :statuses ORDER BY b.slot.startsAt DESC")
    List<Booking> findByPatientAndStatuses(
            @Param("patientId") UUID patientId,
            @Param("statuses") List<Booking.BookingStatus> statuses);
    
    @Query("SELECT b FROM Booking b WHERE b.practitioner.id = :practitionerId " +
           "AND b.slot.startsAt >= :startDate AND b.slot.startsAt <= :endDate " +
           "ORDER BY b.slot.startsAt")
    List<Booking> findByPractitionerAndDateRange(
            @Param("practitionerId") UUID practitionerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.reminderSent = false " +
           "AND b.slot.startsAt BETWEEN :startTime AND :endTime")
    List<Booking> findBookingsNeedingReminder(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.practitioner.id = :practitionerId " +
           "AND b.status = 'COMPLETED' " +
           "AND b.completedAt >= :startDate AND b.completedAt <= :endDate")
    long countCompletedBookingsByPractitionerAndDateRange(
            @Param("practitionerId") UUID practitionerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM Booking b WHERE b.patientPhone = :phone " +
           "ORDER BY b.createdAt DESC")
    List<Booking> findGuestBookingsByPhone(@Param("phone") String phone);
}