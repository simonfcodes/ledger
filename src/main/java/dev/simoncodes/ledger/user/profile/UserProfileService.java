package dev.simoncodes.ledger.user.profile;

import dev.simoncodes.ledger.common.exception.BadRequestException;
import dev.simoncodes.ledger.common.exception.ResourceNotFoundException;
import dev.simoncodes.ledger.currency.CurrencyRepository;

import dev.simoncodes.ledger.user.profile.dto.ProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepo;
    private final CurrencyRepository currencyRepo;

    public UserProfile getUserProfile(UUID userId) {
        return userProfileRepo.findUserProfileByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Could not find user profile for userId {}", userId);
                    return new ResourceNotFoundException("User profile not found for user ID: " + userId);
                });
    }

    public UserProfile createUserProfile(UUID userId, ProfileRequest req) {
        if (userProfileRepo.existsByUserId(userId)) {
            throw new BadRequestException("User profile already exists for user ID: " + userId);
        }
        validateProfileRequest(req);

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setDisplayName(req.displayName());
        profile.setBaseCurrencyCode(req.baseCurrencyCode());
        profile.setTimezone(req.timezone());
        profile.setDateFormat(req.dateFormat());
        profile.setNumberFormat(req.numberFormat());
        return userProfileRepo.save(profile);
    }

    public UserProfile updateUserProfile(UUID userId, ProfileRequest req) {
        Optional<UserProfile> existing = userProfileRepo.findUserProfileByUserId(userId);
        if (existing.isEmpty()) {
            return createUserProfile(userId, req);
        }
        validateProfileRequest(req);
        UserProfile profile = existing.get();
        profile.setDisplayName(req.displayName());
        profile.setBaseCurrencyCode(req.baseCurrencyCode());
        profile.setTimezone(req.timezone());
        profile.setDateFormat(req.dateFormat());
        profile.setNumberFormat(req.numberFormat());
        return userProfileRepo.save(profile);
    }

    private void validateProfileRequest(ProfileRequest req) {
        if (!currencyRepo.existsById(req.baseCurrencyCode())) {
            throw new BadRequestException("Currency code not found: " + req.baseCurrencyCode());
        }
        try {
            ZoneId.of(req.timezone());
        } catch (DateTimeException e) {
            throw new BadRequestException("Invalid timezone: " + req.timezone());
        }
    }
}
