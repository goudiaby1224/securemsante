package sn.msante.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * Tenant entity for multi-tenancy support
 * Each tenant represents a clinic, hospital, or healthcare organization
 */
@Entity
@Table(name = "tenant")
@Data
@EqualsAndHashCode(callSuper = true)
public class Tenant extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode = "SN";
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantStatus status = TenantStatus.ACTIVE;
    
    @Column(name = "settings", columnDefinition = "jsonb")
    private String settings; // JSON configuration for tenant-specific settings
    
    public enum TenantStatus {
        ACTIVE, SUSPENDED, INACTIVE
    }
}