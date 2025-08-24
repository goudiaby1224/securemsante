package sn.goudiaby.msante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.goudiaby.msante.model.Appointment;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByDoctorId(Long doctorId);
    
    List<Appointment> findByPatientIdAndStatus(Long patientId, Appointment.Status status);
    
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, Appointment.Status status);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.user.email = :email")
    List<Appointment> findByPatientEmail(@Param("email") String email);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.user.email = :email")
    List<Appointment> findByDoctorEmail(@Param("email") String email);
    
    @Query("SELECT a FROM Appointment a WHERE (a.patient.user.email = :email OR a.doctor.user.email = :email) " +
           "AND a.availability.startTime > :now " +
           "ORDER BY a.availability.startTime ASC")
    List<Appointment> findUpcomingAppointments(@Param("email") String email, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE DATE(a.availability.startTime) = :date")
    Long countByDate(@Param("date") java.time.LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE DATE(a.availability.startTime) BETWEEN :startDate AND :endDate")
    Long countByDateRange(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId")
    Long countByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = :status")
    Long countByDoctorIdAndStatus(@Param("doctorId") Long doctorId, @Param("status") String status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId")
    Long countByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status")
    Long countByPatientIdAndStatus(@Param("patientId") Long patientId, @Param("status") String status);

    @Query("SELECT DISTINCT a.doctor.specialty FROM Appointment a WHERE a.patient.id = :patientId ORDER BY COUNT(a) DESC")
    List<String> findPreferredSpecialtiesByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT DISTINCT a.doctor.id FROM Appointment a WHERE a.patient.id = :patientId ORDER BY COUNT(a) DESC")
    List<Long> findPreferredDoctorsByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT DATE(a.availability.startTime) as date, COUNT(a) as count FROM Appointment a WHERE DATE(a.availability.startTime) BETWEEN :startDate AND :endDate GROUP BY DATE(a.availability.startTime) ORDER BY date")
    List<Object> getDailyAppointmentCounts(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    @Query("SELECT YEAR(a.availability.startTime) as year, MONTH(a.availability.startTime) as month, COUNT(a) as count FROM Appointment a WHERE DATE(a.availability.startTime) BETWEEN :startDate AND :endDate GROUP BY YEAR(a.availability.startTime), MONTH(a.availability.startTime) ORDER BY year, month")
    List<Object> getMonthlyAppointmentCounts(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    @Query("SELECT a.doctor.specialty as specialty, COUNT(a) as count FROM Appointment a GROUP BY a.doctor.specialty ORDER BY count DESC")
    List<Object> getAppointmentCountsBySpecialty();

    @Query("SELECT a FROM Appointment a WHERE DATE(a.availability.startTime) = :date")
    List<Appointment> findByDate(@Param("date") java.time.LocalDate date);
}