package dev.simoncodes.ledger.transaction.dto;

import dev.simoncodes.ledger.transaction.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateTransactionRequest(
        @NotNull(message = "An amount is required for all transactions")
        @DecimalMin(value = "0.01", message = "A transaction cannot be set to zero")
        BigDecimal amount,
        @NotNull(message = "The direction of the transaction (CREDIT or DEBIT) must be provided to update a transaction")
        TransactionType direction,
        @NotNull(message = "A transaction date must be provided while updating a transaction")
        @PastOrPresent(message = "The transaction date cannot be in the future")
        LocalDate transactionDate,
        @Size(max = 500, message = "The description field must be 500 characters or less")
        String description,
        @Size(max = 255, message = "The reference field must be 255 characters or less")
        String reference,
        LocalDate postedDate,
        UUID categoryId,
        String notes,
        @DecimalMin(value = "0.01", message = "A transaction cannot be updated to an amount less than 0.01 (original amount)")
        BigDecimal originalAmount,
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be a valid ISO 4217 code (3 uppercase letters)")
        String originalCurrencyCode
) {
    @AssertTrue(message = "originalAmount and originalCurrencyCode must both be provided or both be null")
    public boolean isFxFieldsConsistent() {
        return (originalAmount == null) == (originalCurrencyCode == null);
    }
}
