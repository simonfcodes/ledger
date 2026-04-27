package dev.simoncodes.ledger.transaction.pagination;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionCursor(
        LocalDate transactionDate,
        Instant createdAt,
        UUID id
) {
}