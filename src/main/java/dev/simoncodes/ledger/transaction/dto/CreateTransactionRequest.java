package dev.simoncodes.ledger.transaction.dto;

import dev.simoncodes.ledger.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull(message = "An amount must be provided to create a transaction")
        @DecimalMin(value = "0.01", message = "A transaction cannot be created for an amount less than 0.01")
        BigDecimal amount,
        @NotNull(message = "The direction of the transaction (CREDIT or DEBIT) must be provided to create a transaction")
        TransactionType direction,
        @NotNull(message = "A transaction date must be provided to create a transaction")
        @PastOrPresent(message = "The transaction date cannot be in the future")
        LocalDate transactionDate,
        @Size(max = 500, message = "The description field must be 500 characters or less")
        String description,
        @Size(max = 255, message = "The reference field must be 255 characters or less")
        String reference,
        LocalDate postedDate,
        UUID categoryId,
        String notes,
        @DecimalMin(value = "0.01", message = "A transaction cannot be created for an amount less than 0.01 (original amount)")
        BigDecimal originalAmount,
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be a valid ISO 4217 code (3 uppercase letters)")
        String originalCurrencyCode
) {
    @AssertTrue(message = "originalAmount and originalCurrencyCode must both be provided or both be null")
    public boolean isFxFieldsConsistent() {
        return (originalAmount == null) == (originalCurrencyCode == null);
    }
}
