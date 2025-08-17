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
}