package dev.simoncodes.ledger.common;

public final class StringUtils {
    public static String nullIfBlank(String s) {
        return (s == null || s.isBlank() ? null : s);
    }
}
