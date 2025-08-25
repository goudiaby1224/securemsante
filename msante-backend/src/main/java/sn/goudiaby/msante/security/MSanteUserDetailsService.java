package sn.goudiaby.msante.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.goudiaby.msante.model.User;
import sn.goudiaby.msante.repository.UserRepository;

@Service
public class MSanteUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Manual constructor to ensure compilation works
    public MSanteUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user;
    }
}