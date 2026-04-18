package dev.simoncodes.ledger.auth;

import dev.simoncodes.ledger.auth.jwt.JwtService;
import dev.simoncodes.ledger.auth.refresh.RefreshResult;
import dev.simoncodes.ledger.auth.refresh.RefreshToken;
import dev.simoncodes.ledger.auth.refresh.RefreshTokenService;
import dev.simoncodes.ledger.user.User;
import dev.simoncodes.ledger.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtSvc;
    private final RefreshTokenService refreshTokenSvc;

    public AuthTokenSet login(String email, String password) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        boolean verified = encoder.matches(password, user.getPasswordHash());
        if (!verified) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if (!user.isEmailVerified()) throw new UnverifiedEmailException(user.getEmail());
        // add MFA here
        String accessToken = jwtSvc.generateAccessToken(user.getId());
        String refreshToken = refreshTokenSvc.createRefreshToken(user.getId());

        return new AuthTokenSet(accessToken, refreshToken);
    }

    public AuthTokenSet refresh(String refreshToken) {
        RefreshResult result = refreshTokenSvc.rotateRefreshToken(refreshToken);
        String newAccessToken = jwtSvc.generateAccessToken(result.userId());

        return new AuthTokenSet(newAccessToken, result.refreshToken());
    }
}
