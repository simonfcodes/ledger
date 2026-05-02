package dev.simoncodes.ledger.account;

import dev.simoncodes.ledger.account.dto.CreateAccountRequest;
import dev.simoncodes.ledger.account.dto.UpdateAccountRequest;
import dev.simoncodes.ledger.account.view.AccountView;
import dev.simoncodes.ledger.user.UserDetailsAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountSvc;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountView createAccount(
            @AuthenticationPrincipal UserDetailsAdapter principal,
            @Valid @RequestBody CreateAccountRequest req
    ) {
        return accountSvc.createAccount(principal.getUserId(), req);
    }

    @GetMapping("/{id}")
    public AccountView getAccount(
            @AuthenticationPrincipal UserDetailsAdapter principal,
            @PathVariable UUID id
    ) {
        return accountSvc.getAccount(principal.getUserId(), id);
    }

    @GetMapping
    public List<AccountView> listAccounts(
            @AuthenticationPrincipal UserDetailsAdapter principal
    ) {
        return accountSvc.listAccounts(principal.getUserId());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountView updateAccount(
            @AuthenticationPrincipal UserDetailsAdapter principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest req
    ) {
        return accountSvc.updateAccount(principal.getUserId(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            @AuthenticationPrincipal UserDetailsAdapter principal,
            @PathVariable UUID id
    ) {
        accountSvc.deleteAccount(principal.getUserId(), id);
    }
}
