package dev.simoncodes.ledger.auth;


public record OldLoginResult(
        boolean mfaRequired,
        boolean mfaSetupRequired,
        String mfaToken
) {
}
