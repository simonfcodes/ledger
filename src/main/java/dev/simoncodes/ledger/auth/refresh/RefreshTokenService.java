package dev.simoncodes.ledger.auth.refresh;

import dev.simoncodes.ledger.config.RefreshProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshRepo;
    private final RefreshProperties refreshProps;

    private final SecureRandom random = new SecureRandom();

    public String createRefreshToken(UUID uuid) {
        try {

            byte[] randomToken = random.generateSeed(32);
            String returnToken = HexFormat.of().formatHex(randomToken);
            String hashedToken = hashToken(returnToken);

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUserId(uuid);
            refreshToken.setTokenHash(hashedToken);
            refreshToken.setExpiresAt(Instant.now().plusMillis(refreshProps.refreshTokenExpiry()));
            refreshRepo.save(refreshToken);
            return returnToken;
        } catch (Exception e) {
            throw new RefreshTokenException("Failed to create refresh token: " + e.getMessage(), e.getCause());
        }
    }

    public RefreshResult rotateRefreshToken(String refreshToken) {
        try {
            RefreshToken storedToken = validateRefreshToken(refreshToken);
            UUID userId = storedToken.getUserId();
            refreshRepo.delete(storedToken);
            return new RefreshResult(
                createRefreshToken(userId),
                userId
            );
        } catch (RefreshTokenException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RefreshTokenException("Failed to rotate refresh token: " + e.getMessage(), e.getCause());
        }
    }

    public RefreshToken validateRefreshToken(String refreshToken) {
        String hashedToken = hashToken(refreshToken);
        RefreshToken storedToken =  refreshRepo.findByTokenHash(hashedToken).orElseThrow(() -> new RefreshTokenException("Invalid refresh token"));
        if (tokenExpired(storedToken)) {
            throw new RefreshTokenException("Token has expired");
        }
        return storedToken;
    }

    public void revokeAllRefreshTokens(UUID userId) {
        refreshRepo.deleteAllByUserId(userId);
    }

    private boolean tokenExpired(RefreshToken refreshToken) {
        return Instant.now().isAfter(refreshToken.getExpiresAt());
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] rawBytes = token.getBytes(StandardCharsets.UTF_8);
            byte[] hashedTokenBytes = digest.digest(rawBytes);
            return HexFormat.of().formatHex(hashedTokenBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}