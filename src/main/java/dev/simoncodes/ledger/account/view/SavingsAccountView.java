package dev.simoncodes.ledger.account.view;

import dev.simoncodes.ledger.account.AccountType;
import dev.simoncodes.ledger.account.ConnectionType;
import dev.simoncodes.ledger.account.entity.SavingsDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SavingsAccountView(
        UUID id,
        UUID userId,
        UUID institutionId,
        String name,
        AccountType type,
        String currencyCode,
        String countryCode,
        BigDecimal currentBalance,
        Integer displayOrder,
        ConnectionType connectionType,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        SavingsDetails details
) implements AccountView {
}
