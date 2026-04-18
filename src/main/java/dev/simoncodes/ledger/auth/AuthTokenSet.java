package dev.simoncodes.ledger.auth;

public record AuthTokenSet(
        String accessToken,
        String refreshToken
) {}
