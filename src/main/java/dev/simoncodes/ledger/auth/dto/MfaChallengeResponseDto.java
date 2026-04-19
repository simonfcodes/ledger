package dev.simoncodes.ledger.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MfaChallengeResponseDto(
        String error,
        @JsonProperty("mfa_token")
        String mfaToken,
        @JsonProperty("mfa_setup_required")
        boolean mfaSetupRequired
) { }
