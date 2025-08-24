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

    @Query("SELECT DISTINCT d.specialty FROM Doctor d WHERE d.specialty IS NOT NULL")
    List<String> findAllSpecialties();

    @Query("SELECT DISTINCT d.department FROM Doctor d WHERE d.department IS NOT NULL")
    List<String> findAllDepartments();

    @Query("SELECT d FROM Doctor d WHERE " +
           "(:specialty IS NULL OR d.specialty = :specialty) AND " +
           "(:department IS NULL OR d.department = :department) AND " +
           "(:minRating IS NULL OR d.rating >= :minRating) AND " +
           "(:gender IS NULL OR d.user.gender = :gender) AND " +
           "(:language IS NULL OR d.languages LIKE %:language%)")
    List<Doctor> findBySearchCriteria(
            @Param("specialty") String specialty,
            @Param("department") String department,
            @Param("minRating") Double minRating,
            @Param("gender") String gender,
            @Param("language") String language
    );

    @Query("SELECT d FROM Doctor d ORDER BY d.rating DESC")
    List<Doctor> findTopRatedDoctors(org.springframework.data.domain.Pageable pageable);

    default List<Doctor> findTopRatedDoctors(Integer limit) {
        return findTopRatedDoctors(org.springframework.data.domain.PageRequest.of(0, limit));
    }
}