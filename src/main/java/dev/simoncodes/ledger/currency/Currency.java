package dev.simoncodes.ledger.currency;

import org.springframework.data.annotation.Id;

public record Currency (
        @Id String code,
        String name,
        String symbol,
        int decimalPlaces
){ }