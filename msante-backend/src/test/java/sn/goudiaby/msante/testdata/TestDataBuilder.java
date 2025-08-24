package sn.goudiaby.msante.testdata;

import sn.goudiaby.msante.model.*;
import sn.goudiaby.msante.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Test Data Builder for Phase 4 Testing
 * 
 * This utility class provides factory methods for creating test entities
 * with realistic data for comprehensive testing scenarios.
 */
public class TestDataBuilder {

    // User-related test data builders
    public static User createTestUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        user.setPhone("+221701234567");
        user.setPassword("TestPassword123!");
        user.setRole(User.Role.PATIENT);
        user.setEnabled(true);
        user.setGender("MALE");
        return user;
    }

    public static User createTestDoctorUser() {
        User user = new User();
        user.setFirstName("Dr. Sarah");
        user.setLastName("Smith");
        user.setEmail("dr.smith@test.com");
        user.setPhone("+221701234569");
        user.setPassword("TestPassword123!");
        user.setRole(User.Role.DOCTOR);
        user.setEnabled(true);
        user.setGender("FEMALE");
        return user;
    }

    public static User createTestAdmin() {
        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail("admin@test.com");
        user.setPhone("+221701234570");
        user.setPassword("AdminPassword123!");
        user.setRole(User.Role.ADMIN);
        user.setEnabled(true);
        user.setGender("MALE");
        return user;
    }

    // Patient-related test data builders
    public static Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPhone("+221701234567");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setGender("MALE");
        patient.setBloodType("O+");
        patient.setEmergencyContact("Jane Doe");
        patient.setEmergencyPhone("+221701234568");
        patient.setAddress("123 Test Street");
        patient.setCity("Dakar");
        patient.setCountry("Senegal");
        patient.setInsuranceProvider("Test Insurance");
        patient.setInsuranceNumber("INS123456");
        patient.setAllergies(Arrays.asList("Penicillin", "Peanuts"));
        patient.setMedicalConditions(Arrays.asList("Hypertension", "Diabetes"));
        patient.setMedications(Arrays.asList("Metformin", "Lisinopril"));
        patient.setMedicalHistory("Family history of diabetes");
        patient.setPreferences("Prefers morning appointments");
        patient.setProfileImage("profile.jpg");
        patient.setIsActive(true);
        return patient;
    }

    // Doctor-related test data builders
    public static Doctor createTestDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Dr. Sarah");
        doctor.setLastName("Smith");
        doctor.setSpecialty("Cardiology");
        doctor.setLicenseNumber("DOC123456");
        doctor.setPhone("+221701234569");
        doctor.setDepartment("Cardiology");
        doctor.setBio("Experienced cardiologist with 10+ years of practice");
        doctor.setEducation("MD from University of Dakar, Cardiology Fellowship");
        doctor.setExperience("10 years in cardiology");
        doctor.setConsultationFee("15000");
        doctor.setLanguages("French, English, Wolof");
        doctor.setWorkingHours("Monday-Friday 8AM-5PM");
        doctor.setRating(4.8);
        doctor.setTotalReviews(150);
        return doctor;
    }

    // Availability-related test data builders
    public static Availability createTestAvailability() {
        Availability availability = new Availability();
        availability.setStartTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(9, 0)));
        availability.setEndTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)));
        availability.setStatus(Availability.Status.AVAILABLE);
        return availability;
    }

    public static List<Availability> createTestAvailabilities() {
        Availability availability1 = new Availability();
        availability1.setStartTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(9, 0)));
        availability1.setEndTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)));
        availability1.setStatus(Availability.Status.AVAILABLE);

        Availability availability2 = new Availability();
        availability2.setStartTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0)));
        availability2.setEndTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(11, 0)));
        availability2.setStatus(Availability.Status.AVAILABLE);

        Availability availability3 = new Availability();
        availability3.setStartTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0)));
        availability3.setEndTime(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(15, 0)));
        availability3.setStatus(Availability.Status.AVAILABLE);

        return Arrays.asList(availability1, availability2, availability3);
    }

    // Appointment-related test data builders
    public static Appointment createTestAppointment() {
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.Status.PENDING);
        return appointment;
    }

    // DTO-related test data builders
    public static RegisterRequestDTO createTestRegisterRequest() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@test.com");
        dto.setPhone("+221701234567");
        dto.setPassword("TestPassword123!");
        dto.setUserType("PATIENT");
        dto.setDateOfBirth("1990-01-01");
        dto.setSpecialty("");
        dto.setLicenseNumber("");
        return dto;
    }

    public static LoginRequestDTO createTestLoginRequest() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("john.doe@test.com");
        dto.setPassword("TestPassword123!");
        return dto;
    }

    public static BookAppointmentRequestDTO createTestBookAppointmentRequest() {
        BookAppointmentRequestDTO dto = new BookAppointmentRequestDTO();
        dto.setAvailabilityId(1L);
        dto.setNotes("Test appointment booking");
        return dto;
    }

    // Test data for different scenarios
    public static class Scenarios {
        
        public static User createPatientWithCompleteProfile() {
            User user = createTestUser();
            Patient patient = createTestPatient();
            user.setPatient(patient);
            patient.setUser(user);
            return user;
        }

        public static User createDoctorWithCompleteProfile() {
            User user = createTestDoctorUser();
            Doctor doctor = createTestDoctor();
            user.setDoctor(doctor);
            doctor.setUser(user);
            return user;
        }

        public static Appointment createConfirmedAppointment() {
            Appointment appointment = createTestAppointment();
            appointment.setStatus(Appointment.Status.CONFIRMED);
            return appointment;
        }

        public static Appointment createCancelledAppointment() {
            Appointment appointment = createTestAppointment();
            appointment.setStatus(Appointment.Status.CANCELLED);
            return appointment;
        }

        public static List<Appointment> createMultipleAppointments() {
            return Arrays.asList(
                    createConfirmedAppointment(),
                    createCancelledAppointment(),
                    createTestAppointment()
            );
        }
    }

    // Performance testing data builders
    public static class Performance {
        
        public static List<User> createBulkUsers(int count) {
            List<User> users = new java.util.ArrayList<>();
            for (int i = 0; i < count; i++) {
                User user = new User();
                user.setEmail("user" + i + "@test.com");
                user.setFirstName("User" + i);
                user.setLastName("Test" + i);
                user.setPassword("Password" + i + "!");
                user.setRole(User.Role.PATIENT);
                user.setEnabled(true);
                users.add(user);
            }
            return users;
        }

        public static List<Appointment> createBulkAppointments(int count) {
            List<Appointment> appointments = new java.util.ArrayList<>();
            for (int i = 0; i < count; i++) {
                Appointment appointment = new Appointment();
                appointment.setStatus(Appointment.Status.PENDING);
                appointments.add(appointment);
            }
            return appointments;
        }
    }

    // Security testing data builders
    public static class Security {
        
        public static RegisterRequestDTO createMaliciousInput() {
            RegisterRequestDTO dto = new RegisterRequestDTO();
            dto.setFirstName("<script>alert('xss')</script>");
            dto.setEmail("'; DROP TABLE users; --");
            dto.setPassword("' OR '1'='1");
            dto.setLastName("Test");
            dto.setUserType("PATIENT");
            dto.setDateOfBirth("1990-01-01");
            dto.setPhone("+221701234567");
            dto.setSpecialty("");
            dto.setLicenseNumber("");
            return dto;
        }

        public static LoginRequestDTO createInvalidCredentials() {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setEmail("invalid@test.com");
            dto.setPassword("wrongpassword");
            return dto;
        }
    }
}
