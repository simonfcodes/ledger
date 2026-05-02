package dev.simoncodes.ledger.user.profile.dto;

import dev.simoncodes.ledger.common.format.DateFormat;
import dev.simoncodes.ledger.common.format.NumberFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotNull
        @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9 '.\\-]+$", message = "Only letters, numbers, apostrophes, periods, and hyphens are allowed in the display name. ")
        String displayName,
        @NotNull
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be three capital letters")
        String baseCurrencyCode,
        @NotNull
        @Size(max = 50, message = "Timezone must be at most 50 characters in length")
        String timezone,
        @NotNull
        DateFormat dateFormat,
        @NotNull
        NumberFormat numberFormat
) {
}
