package sn.msante.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import sn.msante.entity.UserAccount;
import sn.msante.repository.UserAccountRepository;

import java.util.Arrays;
import java.util.Optional;

/**
 * Custom authentication provider for M-Santé
 */
@Component
public class MSanteAuthenticationProvider implements AuthenticationProvider {
    
    private final UserAccountRepository userAccountRepository;
    
    public MSanteAuthenticationProvider(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        Optional<UserAccount> userOpt = userAccountRepository.findByIdentifier(identifier);
        
        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("User not found");
        }
        
        UserAccount user = userOpt.get();
        
        if (!user.isAccountNonLocked()) {
            throw new BadCredentialsException("Account is locked");
        }
        
        if (user.getStatus() != UserAccount.UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active");
        }
        
        // For OTP-based authentication, password validation is handled elsewhere
        // This provider is mainly for JWT token generation after OTP verification
        
        MSantePrincipal principal = new MSantePrincipal(
            user.getId().toString(),
            user.getRole().name(),
            user.getTenant() != null ? user.getTenant().getId().toString() : null
        );
        
        return new UsernamePasswordAuthenticationToken(
            principal,
            null,
            Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}