package sn.msante.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * User account entity supporting multi-role system
 */
@Entity
@Table(name = "user_account")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccount extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "password_hash")
    private String passwordHash;
    
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    @Column(name = "locale", nullable = false)
    private String locale = "fr";
    
    @Column(name = "twofa_enabled", nullable = false)
    private Boolean twofaEnabled = false;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "locked_until")
    private java.time.LocalDateTime lockedUntil;
    
    public enum UserRole {
        PATIENT, PRACTITIONER, ADMIN, SUPERADMIN
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION
    }
    
    public boolean isAccountNonLocked() {
        return lockedUntil == null || lockedUntil.isBefore(java.time.LocalDateTime.now());
    }
    
    public boolean isEmailVerified() {
        return email != null && status != UserStatus.PENDING_VERIFICATION;
    }
}