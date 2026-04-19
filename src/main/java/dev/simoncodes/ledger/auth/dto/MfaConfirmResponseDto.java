package dev.simoncodes.ledger.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MfaConfirmResponseDto(
        @JsonProperty("backup_codes")
        List<String> backupCodes,
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        long expiresIn
) {
}
