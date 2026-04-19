package dev.simoncodes.ledger.auth.totp;

import dev.simoncodes.ledger.common.Base32Encoder;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TotpService {
    private final SecureRandom random = new SecureRandom();

    public String generateSecret() {
        byte[] randomBytes = random.generateSeed(20);
        return Base32Encoder.encode(randomBytes);
    }

    public String generateCode(String mfaSecret, Instant time) {
        long timeStep = time.getEpochSecond() / 30;
        byte[] counterBytes = ByteBuffer.allocate(8).putLong(timeStep).array();

        byte[] keyBytes = Base32Encoder.decode(mfaSecret);
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(keyBytes, "HmacSHA1"));
            byte[] hmac = mac.doFinal(counterBytes);

            int offset = hmac[hmac.length - 1] & 0xf;
            int code = ((hmac[offset] & 0x7F) << 24)
                    | ((hmac[offset + 1] & 0xFF) << 16)
                    | ((hmac[offset + 2] & 0xFF) << 8)
                    | (hmac[offset + 3] & 0xFF);
            int otp = code % 1_000_000;
            return String.format("%06d", otp);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyCode(@NonNull String mfaSecret, @NonNull String submittedCode) {
        Instant now = Instant.now();

        for (int i = -1; i <= 1; i++) {
            String expectedCode = generateCode(mfaSecret, now.plusSeconds(i * 30));
            if (MessageDigest.isEqual(expectedCode.getBytes(), submittedCode.getBytes())) {
                return true;
            }
        }
        return false;
    }

    public List<String> genBackupCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            byte[] randomBytes = random.generateSeed(5);
            String code = Base32Encoder.encode(randomBytes).substring(0, 8);
            codes.add(code);
        }
        return codes;
    }
}
