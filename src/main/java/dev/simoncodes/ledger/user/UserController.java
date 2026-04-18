package dev.simoncodes.ledger.user;

import dev.simoncodes.ledger.user.dto.RegRequestDto;
import dev.simoncodes.ledger.user.dto.RegResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userSvc;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegResponseDto registerUser(@Valid @RequestBody RegRequestDto request) {
        User u = userSvc.registerUser(
                request.email(),
                request.password()
        );
        return new RegResponseDto(
                u.getId(),
                u.getEmail()
        );
    }
}