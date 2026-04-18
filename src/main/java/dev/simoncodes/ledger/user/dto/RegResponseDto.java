package dev.simoncodes.ledger.user.dto;

import java.util.UUID;

public record RegResponseDto(
        UUID id,
        String email
) {
}
