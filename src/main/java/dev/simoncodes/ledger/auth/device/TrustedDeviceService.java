package dev.simoncodes.ledger.auth.device;

import dev.simoncodes.ledger.common.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrustedDeviceService {
    private final TrustedDeviceRepository trustedDeviceRepo;
    private final SecureRandom random = new SecureRandom();

    public boolean verifyTrustedDevice(UUID userId, String deviceToken) {
        String deviceTokenHash = TokenHashUtil.hashToken(deviceToken);
        Optional<TrustedDevice> deviceLookup = trustedDeviceRepo.findByDeviceTokenHash(deviceTokenHash);
        if (deviceLookup.isEmpty()) {
            return false;
        }
        TrustedDevice device = deviceLookup.get();
        if (!device.getUserId().equals(userId)) {
            return false;
        }
        if (Instant.now().isAfter(device.getExpiresAt())) {
            trustedDeviceRepo.delete(device);
            return false;
        }
        device.setLastUsedAt(Instant.now());
        trustedDeviceRepo.save(device);
        return true;
    }

    public String trustNewDevice(UUID userId, String deviceName) {
         String deviceToken = generateDeviceToken();
         String deviceTokenHash = TokenHashUtil.hashToken(deviceToken);

         TrustedDevice device = new TrustedDevice();
         device.setUserId(userId);
         device.setDeviceName(deviceName);
         device.setDeviceTokenHash(deviceTokenHash);
         device.setLastUsedAt(java.time.Instant.now());
         device.setExpiresAt(java.time.Instant.now().plusSeconds(60L * 60 * 24 * 30)); // 30 days
         trustedDeviceRepo.save(device);

         return deviceToken;
    }
    public String generateDeviceToken() {
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }
}
