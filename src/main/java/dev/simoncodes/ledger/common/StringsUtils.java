package dev.simoncodes.ledger.common;

public class StringsUtils {
    public static String nullIfBlank(String string) {
        if (string != null && string.isBlank()) {
            return null;
        }
        return string;
    }
}
