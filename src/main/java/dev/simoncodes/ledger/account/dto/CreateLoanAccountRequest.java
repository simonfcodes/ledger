package dev.simoncodes.ledger.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanAccountRequest(
        @NotNull
        UUID institutionId,
        @NotNull
        @Size(min=1, max=255)
        String name,
        @NotNull
        @Pattern(regexp="[A-Z]{3}")
        String currencyCode,
        @NotNull
        @Pattern(regexp="[A-Z]{2}")
        String countryCode,
        BigDecimal openingBalance,
        @NotNull
        @DecimalMin("0.00")
        BigDecimal loanAmount,
        @DecimalMin("0.00")
        BigDecimal interestRate,
        Integer termMonths,
        @DecimalMin("0.00")
        BigDecimal monthlyPayment
) implements CreateAccountRequest {
}
