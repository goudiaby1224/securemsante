package sn.msante.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.msante.entity.UserAccount;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserAccount entities
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    
    Optional<UserAccount> findByEmail(String email);
    
    Optional<UserAccount> findByPhone(String phone);
    
    Optional<UserAccount> findByEmailOrPhone(String email, String phone);
    
    @Query("SELECT u FROM UserAccount u WHERE u.tenant.id = :tenantId AND u.role = :role")
    java.util.List<UserAccount> findByTenantAndRole(@Param("tenantId") UUID tenantId, 
                                                   @Param("role") UserAccount.UserRole role);
    
    @Query("SELECT u FROM UserAccount u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<UserAccount> findByIdentifier(@Param("identifier") String identifier);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
}