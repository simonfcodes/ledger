package dev.simoncodes.ledger.auth.refresh;

import dev.simoncodes.ledger.common.TokenHashUtil;
import dev.simoncodes.ledger.config.RefreshProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

            byte[] randomToken = new byte[32];
            random.nextBytes(randomToken);
            String returnToken = HexFormat.of().formatHex(randomToken);
            String hashedToken = TokenHashUtil.hashToken(returnToken);

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
        String hashedToken = TokenHashUtil.hashToken(refreshToken);
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
}