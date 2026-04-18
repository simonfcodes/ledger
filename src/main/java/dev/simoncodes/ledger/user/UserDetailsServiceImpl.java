package dev.simoncodes.ledger.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;

    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        UUID userId = UUID.fromString(id);
        User u = userRepo.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User with id not found: " + id));
        return new UserDetailsAdapter(u);
    }
}
