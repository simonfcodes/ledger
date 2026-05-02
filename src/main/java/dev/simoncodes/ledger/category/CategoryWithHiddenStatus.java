package dev.simoncodes.ledger.category;

import java.util.UUID;

public record CategoryWithHiddenStatus (
        UUID id,
        UUID userId,
        String displayName,
        UUID parentId,
        String color,
        String icon,
        String code,
        boolean hidden
) {
}
