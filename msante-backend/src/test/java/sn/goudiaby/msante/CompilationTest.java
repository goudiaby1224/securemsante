package sn.goudiaby.msante;

import org.junit.jupiter.api.Test;
import sn.goudiaby.msante.dto.DoctorProfileDTO;
import sn.goudiaby.msante.dto.PatientProfileDTO;
import sn.goudiaby.msante.dto.AdvancedSearchDTO;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class CompilationTest {

    @Test
    public void testBasicCompilation() {
        // Test that all main classes can be instantiated
        assertDoesNotThrow(() -> {
            DoctorProfileDTO doctorDTO = new DoctorProfileDTO();
            PatientProfileDTO patientDTO = new PatientProfileDTO();
            AdvancedSearchDTO searchDTO = new AdvancedSearchDTO();
            
            Doctor doctor = new Doctor();
            Patient patient = new Patient();
            User user = new User();
            
            // Test basic field access
            doctor.setFirstName("John");
            doctor.setLastName("Doe");
            doctor.setRating(4.5);
            doctor.setTotalReviews(25);
            
            patient.setFirstName("Jane");
            patient.setLastName("Smith");
            patient.setIsActive(true);
            
            user.setFirstName("John");
            user.setGender("Male");
            user.setRole(User.Role.DOCTOR);
            
            // Verify values
            assertEquals("John", doctor.getFirstName());
            assertEquals("Doe", doctor.getLastName());
            assertEquals(4.5, doctor.getRating());
            assertEquals(25, doctor.getTotalReviews());
            
            assertEquals("Jane", patient.getFirstName());
            assertEquals("Smith", patient.getLastName());
            assertTrue(patient.getIsActive());
            
            assertEquals("John", user.getFirstName());
            assertEquals("Male", user.getGender());
            assertEquals(User.Role.DOCTOR, user.getRole());
        });
    }
}
