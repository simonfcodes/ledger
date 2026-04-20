package dev.simoncodes.ledger.auth;

import jakarta.annotation.Nullable;

public record AuthTokenSet(
        String accessToken,
        String refreshToken,
        @Nullable String deviceToken
) {
    public AuthTokenSet(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, null);
    }
}