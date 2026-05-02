package dev.simoncodes.ledger.user.profile.view;

import dev.simoncodes.ledger.user.profile.UserProfile;

import java.time.Instant;
import java.util.UUID;

public record UserProfileView(
        UUID userId,
        String displayName,
        String baseCurrencyCode,
        String timezone,
        String dateFormat,
        String numberFormat,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserProfileView from(UserProfile profile) {
        return new UserProfileView(
                profile.getUserId(),
                profile.getDisplayName(),
                profile.getBaseCurrencyCode(),
                profile.getTimezone(),
                profile.getDateFormat(),
                profile.getNumberFormat(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
