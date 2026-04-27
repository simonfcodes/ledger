package dev.simoncodes.ledger.transaction.view;

import java.util.List;

public record TransactionListView(
        List<TransactionView> items,
        String nextCursor,
        boolean hasMore
) {}
