package sn.goudiaby.msante.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import sn.goudiaby.msante.dto.LoginRequestDTO;
import sn.goudiaby.msante.dto.RegisterRequestDTO;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:integrationtestdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class OAuth2IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void testUserRegistrationAndLoginFlow() throws Exception {
        // Test user registration
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("integration@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setUserType("PATIENT");
        registerRequest.setAddress("123 Integration St");
        registerRequest.setPhone("555-9999");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequestDTO> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                baseUrl + "/auth/register", registerEntity, String.class);

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertTrue(registerResponse.getBody().contains("User registered successfully"));

        // Test user login
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("integration@test.com");
        loginRequest.setPassword("password123");

        HttpEntity<LoginRequestDTO> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl + "/auth/login", loginEntity, String.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        
        // Extract JWT token from response header
        String jwtToken = loginResponse.getHeaders().getFirst("Authorization");
        assertNotNull(jwtToken, "JWT token should be present in Authorization header");

        // Test accessing protected endpoint with JWT token
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", jwtToken);
        HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);

        ResponseEntity<String> protectedResponse = restTemplate.exchange(
                baseUrl + "/api/appointments/patient",
                HttpMethod.GET,
                authEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, protectedResponse.getStatusCode());
    }

    @Test
    void testAccessProtectedEndpointWithoutToken() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/api/appointments/patient", String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testAccessProtectedEndpointWithInvalidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer invalid.jwt.token");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/api/appointments/patient",
                HttpMethod.GET,
                entity,
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testRoleBasedAccessControl() throws Exception {
        // Create a patient user
        User patientUser = new User();
        patientUser.setEmail("patient@test.com");
        patientUser.setPassword(passwordEncoder.encode("password123"));
        patientUser.setRole(User.Role.PATIENT);
        patientUser.setEnabled(true);
        userRepository.save(patientUser);

        // Login as patient
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("patient@test.com");
        loginRequest.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDTO> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl + "/auth/login", loginEntity, String.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        String jwtToken = loginResponse.getHeaders().getFirst("Authorization");

        // Try to access doctor-only endpoint as patient
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", jwtToken);
        HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);

        ResponseEntity<String> doctorResponse = restTemplate.exchange(
                baseUrl + "/api/appointments/doctor",
                HttpMethod.GET,
                authEntity,
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, doctorResponse.getStatusCode());
    }

    @Test
    void testInvalidLoginCredentials() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("nonexistent@test.com");
        loginRequest.setPassword("wrongpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequestDTO> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl + "/auth/login", loginEntity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }

    @Test
    void testDuplicateUserRegistration() {
        // Register first user
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail("duplicate@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole("PATIENT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequestDTO> registerEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> firstResponse = restTemplate.postForEntity(
                baseUrl + "/auth/register", registerEntity, String.class);
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        // Try to register same user again
        ResponseEntity<String> secondResponse = restTemplate.postForEntity(
                baseUrl + "/auth/register", registerEntity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, secondResponse.getStatusCode());
        assertTrue(secondResponse.getBody().contains("already exists"));
    }
}