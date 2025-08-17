package sn.goudiaby.msante.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.goudiaby.msante.dto.RegisterRequestDTO;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.DoctorRepository;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRoleEnum());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        // Create role-specific profile
        if (request.getRoleEnum() == User.Role.PATIENT) {
            createPatientProfile(savedUser, request);
        } else if (request.getRoleEnum() == User.Role.DOCTOR) {
            createDoctorProfile(savedUser, request);
        }

        return savedUser;
    }

    private void createPatientProfile(User user, RegisterRequestDTO request) {
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setAddress(request.getAddress());
        patient.setPhone(request.getPhone());
        
        if (request.getBirthDate() != null && !request.getBirthDate().isEmpty()) {
            patient.setBirthDate(LocalDate.parse(request.getBirthDate()));
        }
        
        patientRepository.save(patient);
    }

    private void createDoctorProfile(User user, RegisterRequestDTO request) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialty(request.getSpecialty());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setPhone(request.getPhone());
        
        doctorRepository.save(doctor);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}