package sn.msante.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sn.msante.dto.AuthResponseDto;
import sn.msante.dto.OtpRequestDto;
import sn.msante.dto.OtpVerificationDto;
import sn.msante.entity.UserAccount;
import sn.msante.exception.AuthenticationException;
import sn.msante.exception.InvalidOtpException;
import sn.msante.repository.UserAccountRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Authentication service handling OTP and JWT token generation
 */
@Service
public class AuthenticationService {
    
    private final UserAccountRepository userAccountRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationService notificationService;
    
    @Value("${msante.security.jwt.secret}")
    private String jwtSecret;
    
    @Value("${msante.security.jwt.expiration:900000}")
    private long jwtExpiration;
    
    @Value("${msante.security.jwt.refresh-expiration:3600000}")
    private long refreshExpiration;
    
    @Value("${msante.security.otp.expiration:300}")
    private long otpExpiration;
    
    @Value("${msante.security.otp.max-attempts:3}")
    private int maxOtpAttempts;
    
    private final Random random = new Random();
    
    public AuthenticationService(UserAccountRepository userAccountRepository,
                               RedisTemplate<String, String> redisTemplate,
                               NotificationService notificationService) {
        this.userAccountRepository = userAccountRepository;
        this.redisTemplate = redisTemplate;
        this.notificationService = notificationService;
    }
    
    /**
     * Send OTP to user's phone number
     */
    public void sendOtp(OtpRequestDto request) {
        String phone = normalizePhoneNumber(request.getPhone());
        String otpCode = generateOtpCode();
        
        // Store OTP in Redis with expiration
        String otpKey = "otp:" + phone;
        String attemptKey = "otp_attempts:" + phone;
        
        // Check for rate limiting
        String attempts = redisTemplate.opsForValue().get(attemptKey);
        int currentAttempts = attempts != null ? Integer.parseInt(attempts) : 0;
        
        if (currentAttempts >= maxOtpAttempts) {
            throw new AuthenticationException("Too many OTP attempts. Please try again later.");
        }
        
        // Store OTP and increment attempts
        redisTemplate.opsForValue().set(otpKey, otpCode, Duration.ofSeconds(otpExpiration));
        redisTemplate.opsForValue().set(attemptKey, String.valueOf(currentAttempts + 1), 
                                      Duration.ofSeconds(otpExpiration));
        
        // Send OTP via notification service
        notificationService.sendOtp(phone, otpCode, request.getChannel());
    }
    
    /**
     * Verify OTP and generate JWT tokens
     */
    public AuthResponseDto verifyOtp(OtpVerificationDto request) {
        String phone = normalizePhoneNumber(request.getPhone());
        String otpKey = "otp:" + phone;
        
        // Verify OTP
        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        if (storedOtp == null || !storedOtp.equals(request.getCode())) {
            throw new InvalidOtpException("Invalid or expired OTP code");
        }
        
        // Clear OTP from Redis
        redisTemplate.delete(otpKey);
        redisTemplate.delete("otp_attempts:" + phone);
        
        // Find or create user account
        UserAccount user = findOrCreateUser(phone, request.getTenantId());
        
        // Generate JWT tokens
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        
        return new AuthResponseDto(
            accessToken,
            refreshToken,
            jwtExpiration / 1000,
            user.getId().toString(),
            user.getRole().name(),
            user.getTenant() != null ? user.getTenant().getId().toString() : null,
            user.getStatus() == UserAccount.UserStatus.ACTIVE
        );
    }
    
    /**
     * Refresh JWT access token
     */
    public AuthResponseDto refreshToken(String refreshToken) {
        try {
            Claims claims = validateRefreshToken(refreshToken);
            String userId = claims.get("user_id", String.class);
            
            Optional<UserAccount> userOpt = userAccountRepository.findById(UUID.fromString(userId));
            if (userOpt.isEmpty()) {
                throw new AuthenticationException("User not found");
            }
            
            UserAccount user = userOpt.get();
            String newAccessToken = generateAccessToken(user);
            
            return new AuthResponseDto(
                newAccessToken,
                refreshToken, // Keep the same refresh token
                jwtExpiration / 1000,
                user.getId().toString(),
                user.getRole().name(),
                user.getTenant() != null ? user.getTenant().getId().toString() : null,
                user.getStatus() == UserAccount.UserStatus.ACTIVE
            );
            
        } catch (Exception e) {
            throw new AuthenticationException("Invalid refresh token");
        }
    }
    
    private String generateOtpCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
    
    private String normalizePhoneNumber(String phone) {
        // Remove spaces, dashes, and parentheses
        phone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        
        // Add Senegal country code if not present
        if (!phone.startsWith("+")) {
            if (phone.startsWith("77") || phone.startsWith("78") || phone.startsWith("76") || phone.startsWith("70")) {
                phone = "+221" + phone;
            } else if (!phone.startsWith("221")) {
                phone = "+221" + phone;
            } else {
                phone = "+" + phone;
            }
        }
        
        return phone;
    }
    
    private UserAccount findOrCreateUser(String phone, String tenantId) {
        Optional<UserAccount> existingUser = userAccountRepository.findByPhone(phone);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        // Create new patient account for first-time users
        UserAccount newUser = new UserAccount();
        newUser.setPhone(phone);
        newUser.setRole(UserAccount.UserRole.PATIENT);
        newUser.setStatus(UserAccount.UserStatus.ACTIVE);
        newUser.setLocale("fr");
        
        // Set tenant if provided
        if (tenantId != null) {
            // In a real implementation, you'd validate and set the tenant
            // For now, we'll leave it null for default tenant
        }
        
        return userAccountRepository.save(newUser);
    }
    
    private String generateAccessToken(UserAccount user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("user_id", user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("tenant_id", user.getTenant() != null ? user.getTenant().getId().toString() : null)
                .claim("phone", user.getPhone())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }
    
    private String generateRefreshToken(UserAccount user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("user_id", user.getId().toString())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key)
                .compact();
    }
    
    private Claims validateRefreshToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}