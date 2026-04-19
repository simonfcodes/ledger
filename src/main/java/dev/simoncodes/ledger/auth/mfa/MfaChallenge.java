package dev.simoncodes.ledger.auth.mfa;

import java.time.Instant;
import java.util.UUID;

public record MfaChallenge(
        UUID userId,
        Instant expiresAt
) {
}
