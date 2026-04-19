package dev.simoncodes.ledger.common;

import lombok.NonNull;

public class Base32Encoder {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    public static String encode(byte[] value) {
        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bitsInBuffer = 0;

        for (byte b : value) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsInBuffer += 8;

            while (bitsInBuffer >= 5) {
                bitsInBuffer -= 5;
                int index = (buffer >> bitsInBuffer) & 0x1F;
                result.append(ALPHABET.charAt(index));
            }
        }

        if (bitsInBuffer > 0) {
            int index = (buffer << (5 - bitsInBuffer)) & 0x1F;
            result.append(ALPHABET.charAt(index));
        }
        return result.toString();
    }

    public static byte[] decode(@NonNull String value) {
        byte[] output = new byte[value.length() * 5 / 8];
        int buffer = 0;
        int bitsInBuffer = 0;
        int outputIndex = 0;
        for (char c : value.toCharArray()) {
            int charIndex = ALPHABET.indexOf(c);
            buffer = (buffer << 5) | (charIndex & 0xFF);
            bitsInBuffer += 5;

            while (bitsInBuffer >= 8) {
                bitsInBuffer -= 8;
                output[outputIndex++] = (byte) ((buffer >> bitsInBuffer) & 0xFF);
            }
        }
        return output;
    }
}
