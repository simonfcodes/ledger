package dev.simoncodes.ledger.auth;

public sealed interface LoginResult
    permits LoginResult.MfaRequired, LoginResult.Authenticated {

    record MfaRequired(String mfaToken, boolean mfaSetupRequired) implements LoginResult {}
    record Authenticated(String accessToken, String refreshToken) implements LoginResult {}
}
