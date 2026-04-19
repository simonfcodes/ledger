package dev.simoncodes.ledger.auth.dto;

public record MfaSetupRequestDto(
        String mfaChallengeToken
) {
}
