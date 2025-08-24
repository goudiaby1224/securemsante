package sn.goudiaby.msante.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.goudiaby.msante.dto.RegisterRequestDTO;
import sn.goudiaby.msante.dto.UpdateProfileRequestDTO;
import sn.goudiaby.msante.dto.ChangePasswordRequestDTO;
import sn.goudiaby.msante.dto.UserProfileResponseDTO;
import sn.goudiaby.msante.exception.InvalidPasswordException;
import sn.goudiaby.msante.exception.UserNotFoundException;
import sn.goudiaby.msante.model.Doctor;
import sn.goudiaby.msante.model.Patient;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.DoctorRepository;
import sn.goudiaby.msante.repository.PatientRepository;
import sn.goudiaby.msante.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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
        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        user.setPhone(request.getPhone());
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
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
            patient.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        }
        
        patientRepository.save(patient);
    }

    private void createDoctorProfile(User user, RegisterRequestDTO request) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialty(request.getSpecialty());
        doctor.setLicenseNumber(request.getLicenseNumber());
        
        doctorRepository.save(doctor);
    }

    public User findByEmail(String email) {
        Optional<User> optionalUser= userRepository.findByEmail(email);
               return optionalUser.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    @Transactional
    public UserProfileResponseDTO getCurrentUserProfile(String email) {
        User user = findByEmail(email);
        return UserProfileResponseDTO.fromUser(user);
    }

    @Transactional
    public UserProfileResponseDTO updateCurrentUserProfile(String email, UpdateProfileRequestDTO updateRequest) {
        User user = findByEmail(email);
        
        // Update basic user information
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setPhone(updateRequest.getPhone());
        
        // Update role-specific profile information
        if (user.getRole() == User.Role.PATIENT) {
            updatePatientProfile(user, updateRequest);
        } else if (user.getRole() == User.Role.DOCTOR) {
            updateDoctorProfile(user, updateRequest);
        }
        
        User savedUser = userRepository.save(user);
        return UserProfileResponseDTO.fromUser(savedUser);
    }

    private void updatePatientProfile(User user, UpdateProfileRequestDTO updateRequest) {
        Patient patient = user.getPatient();
        if (patient == null) {
            patient = new Patient();
            patient.setUser(user);
        }
        
        patient.setFirstName(updateRequest.getFirstName());
        patient.setLastName(updateRequest.getLastName());
        patient.setPhone(updateRequest.getPhone());
        patient.setAddress(updateRequest.getAddress());
        if (updateRequest.getDateOfBirth() != null) {
            patient.setDateOfBirth(updateRequest.getDateOfBirth());
        }
        
        patientRepository.save(patient);
    }

    private void updateDoctorProfile(User user, UpdateProfileRequestDTO updateRequest) {
        Doctor doctor = user.getDoctor();
        if (doctor == null) {
            doctor = new Doctor();
            doctor.setUser(user);
        }
        
        doctor.setSpecialty(updateRequest.getSpecialty());
        doctor.setLicenseNumber(updateRequest.getLicenseNumber());
        
        doctorRepository.save(doctor);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequestDTO changePasswordRequest) {
        User user = findByEmail(email);
        
        // Verify current password
        if (!checkPassword(user, changePasswordRequest.getCurrentPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        
        // Verify new password confirmation
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    public UserProfileResponseDTO getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return UserProfileResponseDTO.fromUser(user);
    }
}