package dev.simoncodes.ledger.auth;

import dev.simoncodes.ledger.auth.jwt.JwtService;
import dev.simoncodes.ledger.auth.mfa.MfaChallenge;
import dev.simoncodes.ledger.auth.mfa.MfaConfirmSetupResponse;
import dev.simoncodes.ledger.auth.mfa.MfaException;
import dev.simoncodes.ledger.auth.mfa.MfaSetupResponse;
import dev.simoncodes.ledger.auth.refresh.RefreshResult;
import dev.simoncodes.ledger.auth.refresh.RefreshTokenService;
import dev.simoncodes.ledger.auth.totp.TotpService;
import dev.simoncodes.ledger.common.Base32Encoder;
import dev.simoncodes.ledger.common.encryption.EncryptedString;
import dev.simoncodes.ledger.common.encryption.EncryptionService;
import dev.simoncodes.ledger.user.User;
import dev.simoncodes.ledger.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final String MFA_URI = "otpauth://totp/Ledger:%s?secret=%s&issuer=Ledger&digits=6&period=30";
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtSvc;
    private final RefreshTokenService refreshTokenSvc;
    private final TotpService totpSvc;
    private final EncryptionService encryptionSvc;
    private final ConcurrentHashMap<String, MfaChallenge> mfaChallenges = new ConcurrentHashMap<>();

    private final SecureRandom random = new SecureRandom();

    public LoginResult login(String email, String password) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        boolean verified = encoder.matches(password, user.getPasswordHash());
        if (!verified) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if (!user.isEmailVerified()) throw new UnverifiedEmailException(user.getEmail());

        String challengeToken = Base32Encoder.encode(random.generateSeed(12));
        mfaChallenges.put(challengeToken, new MfaChallenge(user.getId(), Instant.now().plusSeconds(300)));

        return new LoginResult(
                true,
                !user.isMfaEnabled(),
                challengeToken
        );
    }

    public AuthTokenSet refresh(String refreshToken) {
        RefreshResult result = refreshTokenSvc.rotateRefreshToken(refreshToken);
        String newAccessToken = jwtSvc.generateAccessToken(result.userId());

        return new AuthTokenSet(newAccessToken, result.refreshToken());
    }

    public AuthTokenSet completeMfaLogin(String token, String mfaCode) {
        User u = validateMfaTokenAndGetUser(token);

        if (!u.isMfaEnabled()) {
            mfaChallenges.remove(token);
            throw new MfaException("MFA setup is required for user before login.");
        }
        boolean totpCodeOk = totpSvc.verifyCode(u.getMfaSecret().value(), mfaCode);
        if (!totpCodeOk) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        mfaChallenges.remove(token);

        String accessToken = jwtSvc.generateAccessToken(u.getId());
        String refreshToken = refreshTokenSvc.createRefreshToken(u.getId());
        return new AuthTokenSet(accessToken, refreshToken);
    }

    public MfaSetupResponse initiateMfaSetup(String token) {
        User u = validateMfaTokenAndGetUser(token);
        if (u.isMfaEnabled()) {
            throw new MfaException("MFA is already enabled for this user.");
        }
        String mfaSecret = totpSvc.generateSecret();
        u.setMfaSecret(new EncryptedString(mfaSecret));
        userRepo.save(u);
        return new MfaSetupResponse(
                mfaSecret,
                String.format(MFA_URI, u.getEmail(), mfaSecret)
        );
    }

    public MfaConfirmSetupResponse confirmMfaLogin(String token, String mfaCode) {
        User u = validateMfaTokenAndGetUser(token);
        if (u.isMfaEnabled()) throw new MfaException("MFA is already enabled for this user.");
        boolean totpCodeOk = totpSvc.verifyCode(u.getMfaSecret().value(), mfaCode);
        if (!totpCodeOk) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        u.setMfaEnabled(true);

        List<String> backupCodes = totpSvc.genBackupCodes();
        List<String> bcryptedCodes = new ArrayList<>();
        for (String code : backupCodes) {
            bcryptedCodes.add(encoder.encode(code));
        }
        u.setMfaBackupCodes(bcryptedCodes.toArray(new String[10]));
        userRepo.save(u);

        String accessToken = jwtSvc.generateAccessToken(u.getId());
        String refreshToken = refreshTokenSvc.createRefreshToken(u.getId());
        AuthTokenSet tokens = new AuthTokenSet(accessToken, refreshToken);
        return new MfaConfirmSetupResponse(
                tokens,
                backupCodes
        );
    }

    private User validateMfaTokenAndGetUser(String token) {
        MfaChallenge challenge = mfaChallenges.get(token);
        if (challenge == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        if (challenge.expiresAt().isBefore(Instant.now())) {
            mfaChallenges.remove(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return userRepo.findById(challenge.userId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
