package dev.simoncodes.ledger.transaction;

import dev.simoncodes.ledger.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository txRepo;
    private final AccountRepository accountRepo;

    @Transactional
    public Transaction createOpeningBalance(UUID accountId, BigDecimal openingBalance, String currencyCode) {
        Transaction t = new Transaction();
        TransactionType direction = openingBalance.signum() >= 0 ? TransactionType.CREDIT : TransactionType.DEBIT;
        BigDecimal amount = openingBalance.abs();

        t.setAccountId(accountId);
        t.setAmount(amount);
        t.setDirection(direction);
        t.setCurrencyCode(currencyCode);
        t.setTransactionDate(LocalDate.now());
        t.setOriginalAmount(amount);
        t.setOriginalCurrencyCode(currencyCode);
        t.setRecurring(false);
        t.setDescription("Opening Balance");
        t.setNotes(null);
        t.setReference(null);
        t.setPostedAt(null);
        t.setCategoryId(null);
        t.setSource(TransactionSource.OPENING_BALANCE);
        Transaction saved = txRepo.save(t);

        int rows = accountRepo.adjustBalance(accountId, openingBalance);
        if (rows != 1) {
            throw new IllegalStateException("Failed to update account with ID " + accountId + " with an opening balance.");
        }
        return t;
    }
}
