package dev.simoncodes.ledger.currency;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("currencies")
public record Currency (
        @Id String code,
        String name,
        String symbol,
        int decimalPlaces
){ }