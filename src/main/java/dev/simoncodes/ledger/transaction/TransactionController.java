package dev.simoncodes.ledger.transaction;

import dev.simoncodes.ledger.transaction.dto.CreateTransactionRequest;
import dev.simoncodes.ledger.transaction.dto.TransactionListRequest;
import dev.simoncodes.ledger.transaction.view.TransactionListView;
import dev.simoncodes.ledger.transaction.view.TransactionView;
import dev.simoncodes.ledger.user.UserDetailsAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequestMapping("/accounts/{accountId}/transactions")
@RestController
@Validated
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService txSvc;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionView createTransaction(@AuthenticationPrincipal UserDetailsAdapter principal, @PathVariable UUID accountId, @Valid @RequestBody CreateTransactionRequest req) {
        return TransactionView.fromTransaction(txSvc.createTransaction(principal.getUserId(), accountId, req));
    }

    @GetMapping("/{txId}")
    @ResponseStatus(HttpStatus.OK)
    public TransactionView getTransaction(@AuthenticationPrincipal UserDetailsAdapter principal, @PathVariable UUID accountId, @PathVariable UUID txId) {
        return TransactionView.fromTransaction(txSvc.getTransaction(principal.getUserId(), accountId, txId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public TransactionListView listTransactions(
            @AuthenticationPrincipal UserDetailsAdapter principal,
            @PathVariable UUID accountId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(name = "from", required = false) LocalDate fromDate,
            @RequestParam(name = "to", required = false) LocalDate toDate,
            @RequestParam(required = false) TransactionType direction,
            @RequestParam(required = false) UUID categoryId
    ) {
        TransactionListRequest req = new TransactionListRequest(cursor, size, fromDate, toDate, direction, categoryId);
        return txSvc.listTransactions(principal.getUserId(), accountId, req);
    }
}
