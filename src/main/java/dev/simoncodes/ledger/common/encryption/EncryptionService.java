package dev.simoncodes.ledger.common.encryption;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionService {
    private final EncryptionProperties encryptionProps;
    private SecretKeySpec secretKey;

    private final SecureRandom random = new SecureRandom();

    @PostConstruct
    public void init() {
        System.out.println("Initializing encryption service..." + encryptionProps.key());
        byte[] keyBytes = Base64.getDecoder().decode(encryptionProps.key());
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plainText) {
        byte[] ivBytes = random.generateSeed(12);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] combined = ByteBuffer.allocate(ivBytes.length + cipherBytes.length)
                    .put(ivBytes)
                    .put(cipherBytes)
                    .array();
            return new String(Base64.getEncoder().encode(combined), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String cipherText) {
        byte[] combined = Base64.getDecoder().decode(cipherText);
        ByteBuffer buffer = ByteBuffer.wrap(combined);
        byte[] ivBytes = new byte[12];
        buffer.get(ivBytes);
        byte[] cipherBytes = new byte[buffer.remaining()];
        buffer.get(cipherBytes);

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
