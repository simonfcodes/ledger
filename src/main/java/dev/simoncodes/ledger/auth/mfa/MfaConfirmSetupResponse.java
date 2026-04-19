package dev.simoncodes.ledger.auth.mfa;

import dev.simoncodes.ledger.auth.AuthTokenSet;

import java.util.List;

public record MfaConfirmSetupResponse(
        AuthTokenSet tokens,
        List<String> backupCodes
) {
}
