package dev.simoncodes.ledger.user;

import java.util.UUID;

public record RegResponseDto(
        UUID id,
        String email
) {
}
