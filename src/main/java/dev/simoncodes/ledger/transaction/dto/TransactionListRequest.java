package dev.simoncodes.ledger.transaction.dto;

import dev.simoncodes.ledger.transaction.TransactionType;

import java.time.LocalDate;
import java.util.UUID;

public record TransactionListRequest(
        String cursor,
        int size,
        LocalDate fromDate,
        LocalDate toDate,
        TransactionType direction,
        UUID categoryId
) { }
