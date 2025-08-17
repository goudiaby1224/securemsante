package sn.msante.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

/**
 * Custom principal for M-Santé authentication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MSantePrincipal implements Principal {
    
    private String userId;
    private String role;
    private String tenantId;
    
    @Override
    public String getName() {
        return userId;
    }
    
    public boolean hasRole(String role) {
        return this.role != null && this.role.equalsIgnoreCase(role);
    }
    
    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("SUPERADMIN");
    }
    
    public boolean isPractitioner() {
        return hasRole("PRACTITIONER");
    }
    
    public boolean isPatient() {
        return hasRole("PATIENT");
    }
}