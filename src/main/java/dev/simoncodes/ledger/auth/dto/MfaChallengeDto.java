package dev.simoncodes.ledger.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MfaChallengeDto(
        @JsonProperty("mfa_token")
        String mfaToken,
        String otp,
        @JsonProperty("trust_device")
        Boolean trustDevice
) {
}
