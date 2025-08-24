package sn.goudiaby.msante;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sn.goudiaby.msante.dto.DoctorProfileDTO;
import sn.goudiaby.msante.dto.PatientProfileDTO;
import sn.goudiaby.msante.dto.AdvancedSearchDTO;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Phase3CompilationTest {

    @Test
    public void testPhase3DTOsCanBeInstantiated() {
        // Test that all Phase 3 DTOs can be created
        DoctorProfileDTO doctorDTO = new DoctorProfileDTO();
        assertNotNull(doctorDTO);
        
        PatientProfileDTO patientDTO = new PatientProfileDTO();
        assertNotNull(patientDTO);
        
        AdvancedSearchDTO searchDTO = new AdvancedSearchDTO();
        assertNotNull(searchDTO);
    }

    @Test
    public void testPhase3ModelsCanBeInstantiated() {
        // Test that all Phase 3 models can be created
        Doctor doctor = new Doctor();
        assertNotNull(doctor);
        
        Patient patient = new Patient();
        assertNotNull(patient);
        
        User user = new User();
        assertNotNull(user);
    }

    @Test
    public void testPhase3ModelsHaveRequiredFields() {
        // Test that models have the required fields
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialty("Cardiology");
        doctor.setDepartment("Cardiology");
        doctor.setBio("Experienced cardiologist");
        doctor.setEducation("Medical School");
        doctor.setExperience("10 years");
        doctor.setConsultationFee("100");
        doctor.setLanguages("English, French");
        doctor.setWorkingHours("9 AM - 5 PM");
        doctor.setRating(4.5);
        doctor.setTotalReviews(25);
        
        assertEquals("John", doctor.getFirstName());
        assertEquals("Doe", doctor.getLastName());
        assertEquals("Cardiology", doctor.getSpecialty());
        assertEquals("Cardiology", doctor.getDepartment());
        assertEquals("Experienced cardiologist", doctor.getBio());
        assertEquals("Medical School", doctor.getEducation());
        assertEquals("10 years", doctor.getExperience());
        assertEquals("100", doctor.getConsultationFee());
        assertEquals("English, French", doctor.getLanguages());
        assertEquals("9 AM - 5 PM", doctor.getWorkingHours());
        assertEquals(4.5, doctor.getRating());
        assertEquals(25, doctor.getTotalReviews());
    }

    @Test
    public void testPatientModelHasRequiredFields() {
        Patient patient = new Patient();
        patient.setFirstName("Jane");
        patient.setLastName("Smith");
        patient.setPhone("+1234567890");
        patient.setGender("Female");
        patient.setBloodType("O+");
        patient.setEmergencyContact("John Smith");
        patient.setEmergencyPhone("+1234567891");
        patient.setAddress("123 Main St");
        patient.setCity("New York");
        patient.setCountry("USA");
        patient.setInsuranceProvider("Blue Cross");
        patient.setInsuranceNumber("BC123456");
        patient.setMedicalHistory("No major issues");
        patient.setPreferences("Morning appointments preferred");
        patient.setIsActive(true);
        
        assertEquals("Jane", patient.getFirstName());
        assertEquals("Smith", patient.getLastName());
        assertEquals("+1234567890", patient.getPhone());
        assertEquals("Female", patient.getGender());
        assertEquals("O+", patient.getBloodType());
        assertEquals("John Smith", patient.getEmergencyContact());
        assertEquals("+1234567891", patient.getEmergencyPhone());
        assertEquals("123 Main St", patient.getAddress());
        assertEquals("New York", patient.getCity());
        assertEquals("USA", patient.getCountry());
        assertEquals("Blue Cross", patient.getInsuranceProvider());
        assertEquals("BC123456", patient.getInsuranceNumber());
        assertEquals("No major issues", patient.getMedicalHistory());
        assertEquals("Morning appointments preferred", patient.getPreferences());
        assertTrue(patient.getIsActive());
    }

    @Test
    public void testUserModelHasRequiredFields() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone("+1234567890");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRole(User.Role.DOCTOR);
        user.setEnabled(true);
        user.setGender("Male");
        
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("+1234567890", user.getPhone());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(User.Role.DOCTOR, user.getRole());
        assertTrue(user.getEnabled());
        assertEquals("Male", user.getGender());
    }
}
