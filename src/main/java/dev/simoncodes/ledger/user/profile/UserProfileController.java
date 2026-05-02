package dev.simoncodes.ledger.user.profile;

import dev.simoncodes.ledger.user.UserDetailsAdapter;
import dev.simoncodes.ledger.user.profile.dto.ProfileRequest;
import dev.simoncodes.ledger.user.profile.view.UserProfileView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileSvc;

    @GetMapping
    public UserProfileView getUserProfile(@AuthenticationPrincipal UserDetailsAdapter principal) {
        return UserProfileView.from(userProfileSvc.getUserProfile(principal.getUserId()));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserProfileView createProfile(@AuthenticationPrincipal UserDetailsAdapter principal,
                                         @Valid @RequestBody ProfileRequest req
    ) {
        return UserProfileView.from(userProfileSvc.createUserProfile(
                principal.getUserId(),
                req
        ));
    }

    @PutMapping
    public UserProfileView updateProfile(@AuthenticationPrincipal UserDetailsAdapter principal,
                                         @Valid @RequestBody ProfileRequest req
    ) {
        return UserProfileView.from(userProfileSvc.updateUserProfile(
                principal.getUserId(),
                req
        ));
    }
}
