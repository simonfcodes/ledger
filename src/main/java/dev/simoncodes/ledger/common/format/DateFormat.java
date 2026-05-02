package dev.simoncodes.ledger.common.format;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DateFormat {
    DD_MM_YYYY("DD/MM/YYYY"),
    MM_DD_YYYY("MM/DD/YYYY"),
    YYYY_MM_DD("YYYY-MM-DD");

    private final String pattern;

    DateFormat(String pattern) {
        this.pattern = pattern;
    }

    @JsonValue
    public String getPattern() {
        return pattern;
    }
}
