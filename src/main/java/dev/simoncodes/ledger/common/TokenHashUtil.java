package dev.simoncodes.ledger.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class TokenHashUtil {

    public static String hashToken(String token) {
        try {
            byte[] rawBytes = token.getBytes(StandardCharsets.UTF_8);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedTokenBytes = digest.digest(rawBytes);
            return HexFormat.of().formatHex(hashedTokenBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
