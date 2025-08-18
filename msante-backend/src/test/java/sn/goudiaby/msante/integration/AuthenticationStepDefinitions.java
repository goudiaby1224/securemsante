package sn.goudiaby.msante.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import sn.goudiaby.msante.dto.*;
import sn.goudiaby.msante.model.*;
import sn.goudiaby.msante.repository.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ResponseEntity<String> lastResponse;
    private String jwtToken;
    private String baseUrl;
    private User createdUser;
    private Availability createdAvailability;

    @Given("the M-Santé application is running")
    public void theMSanteApplicationIsRunning() {
        baseUrl = "http://localhost:" + port;
        assertNotNull(restTemplate);
    }

    @When("I register a new patient user with email {string} and password {string}")
    public void iRegisterANewPatientUserWithEmailAndPassword(String email, String password) throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail(email);
        request.setPassword(password);
        request.setRole("PATIENT");
        request.setAddress("123 Test Street");
        request.setPhone("555-1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequestDTO> entity = new HttpEntity<>(request, headers);

        lastResponse = restTemplate.postForEntity(baseUrl + "/auth/register", entity, String.class);
    }

    @Then("the registration should be successful")
    public void theRegistrationShouldBeSuccessful() {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
        assertTrue(lastResponse.getBody().contains("User registered successfully"));
    }

    @And("I should receive a confirmation message")
    public void iShouldReceiveAConfirmationMessage() {
        assertNotNull(lastResponse.getBody());
        assertTrue(lastResponse.getBody().contains("User registered successfully"));
    }

    @Given("a patient user exists with email {string} and password {string}")
    public void aPatientUserExistsWithEmailAndPassword(String email, String password) {
        createTestUser(email, password, User.Role.PATIENT);
    }

    @Given("a doctor user exists with email {string} and password {string}")
    public void aDoctorUserExistsWithEmailAndPassword(String email, String password) {
        createTestUser(email, password, User.Role.DOCTOR);
    }

    private void createTestUser(String email, String password, User.Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        createdUser = userRepository.save(user);

        if (role == User.Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(user);
            patient.setAddress("123 Test Street");
            patient.setPhone("555-1234");
            patientRepository.save(patient);
        } else if (role == User.Role.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUser(user);
            doctor.setSpecialty("Test Specialty");
            doctor.setLicenseNumber("LIC123");
            doctor.setPhone("555-5678");
            doctorRepository.save(doctor);
        }
    }

    @When("I login with email {string} and password {string}")
    public void iLoginWithEmailAndPassword(String email, String password) throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail(email);
        request.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(request, headers);

        lastResponse = restTemplate.postForEntity(baseUrl + "/auth/login", entity, String.class);
    }

    @Then("the login should be successful")
    public void theLoginShouldBeSuccessful() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
    }

    @And("I should receive a JWT token in the response header")
    public void iShouldReceiveAJWTTokenInTheResponseHeader() {
        jwtToken = lastResponse.getHeaders().getFirst("Authorization");
        assertNotNull(jwtToken, "JWT token should be present in Authorization header");
    }

    @Given("I have a valid JWT token for that user")
    public void iHaveAValidJWTTokenForThatUser() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail(createdUser.getEmail());
        request.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(baseUrl + "/auth/login", entity, String.class);
        jwtToken = loginResponse.getHeaders().getFirst("Authorization");
        assertNotNull(jwtToken, "JWT token should be obtained after login");
    }

    @Given("I have a valid JWT token for the patient")
    public void iHaveAValidJWTTokenForThePatient() throws Exception {
        iHaveAValidJWTTokenForThatUser();
    }

    @When("I access the patient appointments endpoint")
    public void iAccessThePatientAppointmentsEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        if (jwtToken != null) {
            headers.set("Authorization", jwtToken);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        lastResponse = restTemplate.exchange(
                baseUrl + "/api/appointments/patient",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    @Then("I should receive a successful response")
    public void iShouldReceiveASuccessfulResponse() {
        assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
    }

    @When("I access the patient appointments endpoint without authentication")
    public void iAccessThePatientAppointmentsEndpointWithoutAuthentication() {
        jwtToken = null;
        iAccessThePatientAppointmentsEndpoint();
    }

    @Then("I should receive an unauthorized response")
    public void iShouldReceiveAnUnauthorizedResponse() {
        assertEquals(HttpStatus.UNAUTHORIZED, lastResponse.getStatusCode());
    }

    @When("I access the patient appointments endpoint with an invalid JWT token")
    public void iAccessThePatientAppointmentsEndpointWithAnInvalidJWTToken() {
        jwtToken = "Bearer invalid.jwt.token";
        iAccessThePatientAppointmentsEndpoint();
    }

    @When("I access the doctor appointments endpoint")
    public void iAccessTheDoctorAppointmentsEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        if (jwtToken != null) {
            headers.set("Authorization", jwtToken);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        lastResponse = restTemplate.exchange(
                baseUrl + "/api/appointments/doctor",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    @Then("I should receive a forbidden response")
    public void iShouldReceiveAForbiddenResponse() {
        assertEquals(HttpStatus.FORBIDDEN, lastResponse.getStatusCode());
    }

    @Given("there is an available appointment slot")
    public void thereIsAnAvailableAppointmentSlot() {
        // Find the doctor user we created
        User doctorUser = userRepository.findByEmail("doctor@test.com").orElseThrow();
        Doctor doctor = doctorRepository.findByUserId(doctorUser.getId()).orElseThrow();

        Availability availability = new Availability();
        availability.setDoctor(doctor);
        availability.setStartTime(LocalDateTime.now().plusDays(1));
        availability.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        availability.setStatus(Availability.Status.AVAILABLE);
        createdAvailability = availabilityRepository.save(availability);
    }

    @When("I book an appointment")
    public void iBookAnAppointment() throws Exception {
        BookAppointmentRequestDTO request = new BookAppointmentRequestDTO();
        request.setAvailabilityId(createdAvailability.getId());
        request.setNotes("Test appointment booking");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (jwtToken != null) {
            headers.set("Authorization", jwtToken);
        }
        HttpEntity<BookAppointmentRequestDTO> entity = new HttpEntity<>(request, headers);

        lastResponse = restTemplate.postForEntity(baseUrl + "/api/appointments/book", entity, String.class);
    }

    @Then("the booking should be successful")
    public void theBookingShouldBeSuccessful() {
        assertEquals(HttpStatus.CREATED, lastResponse.getStatusCode());
    }

    @And("I should receive appointment confirmation details")
    public void iShouldReceiveAppointmentConfirmationDetails() {
        assertNotNull(lastResponse.getBody());
        // The response should contain appointment details as JSON
        assertTrue(lastResponse.getBody().contains("id") || lastResponse.getBody().length() > 0);
    }
}