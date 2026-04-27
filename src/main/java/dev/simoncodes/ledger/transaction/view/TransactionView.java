package dev.simoncodes.ledger.transaction.view;

import dev.simoncodes.ledger.transaction.Transaction;
import dev.simoncodes.ledger.transaction.TransactionSource;
import dev.simoncodes.ledger.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionView(
        UUID id,
        UUID accountId,
        BigDecimal amount,
        String currencyCode,
        TransactionType direction,
        LocalDate transactionDate,
        BigDecimal originalAmount,
        String originalCurrencyCode,
        String description,
        String notes,
        String reference,
        LocalDate postedDate,
        UUID categoryId,
        TransactionSource source,
        Instant createdAt,
        Instant updatedAt
) {
    public static TransactionView fromTransaction(Transaction t) {
        return new TransactionView(
                t.getId(),
                t.getAccountId(),
                t.getAmount(),
                t.getCurrencyCode(),
                t.getDirection(),
                t.getTransactionDate(),
                t.getOriginalAmount(),
                t.getOriginalCurrencyCode(),
                t.getDescription(),
                t.getNotes(),
                t.getReference(),
                t.getPostedDate(),
                t.getCategoryId(),
                t.getSource(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
