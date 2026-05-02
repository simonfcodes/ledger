package dev.simoncodes.ledger.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateSavingsAccountRequest(
        @NotNull
        @Size(min=1, max=255)
        String name,
        @NotNull
        Boolean active,
        @NotNull
        Integer displayOrder,
        @NotNull
        @DecimalMin("0.00")
        BigDecimal interestRate
) implements UpdateAccountRequest {
}
