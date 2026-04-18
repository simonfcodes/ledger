package dev.simoncodes.ledger.auth.refresh;

import java.util.UUID;

public record RefreshResult(
        String refreshToken,
        UUID userId
) {
}
