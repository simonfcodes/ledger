package dev.simoncodes.ledger.user;

import dev.simoncodes.ledger.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public User registerUser(String email, String password) {
        checkEmail(email);
        User u = createUserObject(email, password);
        sendEmailVerification(email);
        return userRepo.save(u);
    }

    private void checkEmail(String email) {
        Optional<User> u = userRepo.findByEmail(email);
        if (u.isPresent()) {
            throw new ConflictException("Email address provided is already registered.");
        }
    }

    private User createUserObject(String email, String password) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        return u;
    }

    private void sendEmailVerification(String email) {
        // TODO: implement send email verification
    }
}
