package dev.simoncodes.ledger.common.format;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NumberFormat {
    COMMA_DOT("1,000.00"),
    DOT_COMMA("1.000,00"),
    SPACE_COMMA("1 000,00");

    private final String pattern;

    NumberFormat(String pattern) {
        this.pattern = pattern;
    }

    @JsonValue
    public String getPattern() {
        return pattern;
    }
}
