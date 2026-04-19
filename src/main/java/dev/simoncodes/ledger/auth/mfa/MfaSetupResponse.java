package dev.simoncodes.ledger.auth.mfa;

public record MfaSetupResponse(
        String mfaSecret,
        String mfaQrUri
) {
}
