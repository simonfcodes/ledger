package dev.simoncodes.ledger.auth.dto;

public record MfaChallengeDto(
        String mfaToken,
        String mfaCode
) {
}
