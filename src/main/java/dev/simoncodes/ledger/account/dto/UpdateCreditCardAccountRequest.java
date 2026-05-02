package dev.simoncodes.ledger.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCreditCardAccountRequest(
        @NotNull
        @Size(min=1, max=255)
        String name,
        @NotNull
        Boolean active,
        @NotNull
        Integer displayOrder,
        @NotNull
        @DecimalMin("0.00")
        BigDecimal creditLimit,
        @NotNull
        @DecimalMin("0.00")
        BigDecimal apr,
        BigDecimal lastStatementBalance,
        LocalDate lastStatementDate,
        LocalDate nextPaymentDueDate,
        @DecimalMin("0.00")
        BigDecimal nextPaymentAmount
) implements UpdateAccountRequest {
}
