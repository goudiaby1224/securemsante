package sn.goudiaby.msante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.goudiaby.msante.model.Doctor;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByUserId(Long userId);
    
    Optional<Doctor> findByUserEmail(String email);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialty LIKE %:specialty%")
    List<Doctor> findBySpecialtyContaining(@Param("specialty") String specialty);
}