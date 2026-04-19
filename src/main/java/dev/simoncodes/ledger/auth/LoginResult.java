package dev.simoncodes.ledger.auth;


public record LoginResult(
        boolean mfaRequired,
        boolean mfaSetupRequired,
        String mfaToken
) {
}
