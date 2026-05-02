package dev.simoncodes.ledger.category.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoryRequest(
        @NotNull
        @Size(min = 1, max = 100, message = "Display name must be between 1 and 100 characters in length")
        String displayName,
        UUID parentId,
        @NotNull
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color must be a valid hexadecimal value with a preceding hashtag.")
        String color,
        @Size(min = 1, max = 50, message = "Icon name must be between 1 and 50 characters in length")
        String icon
) {
}
