package dev.simoncodes.ledger.transaction;

import dev.simoncodes.ledger.account.AccountRepository;
import dev.simoncodes.ledger.account.entity.Account;
import dev.simoncodes.ledger.category.CategoryRepository;
import dev.simoncodes.ledger.common.exception.ConflictException;
import dev.simoncodes.ledger.common.exception.ResourceNotFoundException;
import dev.simoncodes.ledger.currency.CurrencyRepository;
import dev.simoncodes.ledger.transaction.dto.CreateTransactionRequest;
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
    private final CurrencyRepository currencyRepo;
    private final CategoryRepository categoryRepo;

    @Transactional
    public Transaction createTransaction(UUID userId, CreateTransactionRequest req) {
        Account account = accountRepo.findById(req.accountId())
                .filter(a -> a.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + req.accountId()));
        if (!account.isActive()) {
            throw new ConflictException("Account is inactive: " + account.getId());
        }
        if (req.categoryId() != null && !categoryRepo.existsByIdAndUserId(req.categoryId(), userId)) {
            throw new ResourceNotFoundException("Category not found with id: " + req.categoryId());
        }
        if (req.originalCurrencyCode() != null && !currencyRepo.existsById(req.originalCurrencyCode())) {
            throw new ResourceNotFoundException("Currency not found with id: " + req.originalCurrencyCode());
        }

        Transaction t = new Transaction();
        t.setAccountId(account.getId());
        t.setAmount(req.amount());
        t.setDirection(req.direction());
        t.setCurrencyCode(account.getCurrencyCode());
        t.setTransactionDate(req.transactionDate());
        t.setDescription(req.description());
        t.setReference(req.reference());
        t.setPostedDate(req.postedDate());
        t.setCategoryId(req.categoryId());
        t.setNotes(req.notes());
        t.setSource(TransactionSource.MANUAL);
        t.setOriginalAmount(req.originalAmount() == null ? req.amount() : req.originalAmount());
        t.setOriginalCurrencyCode(req.originalCurrencyCode() == null ? account.getCurrencyCode() : req.originalCurrencyCode());
        t.setRecurring(false);

        BigDecimal delta = req.direction() == TransactionType.CREDIT ? req.amount() : req.amount().negate();
        Transaction saved =  txRepo.save(t);
        int rows = accountRepo.adjustBalance(account.getId(), delta);
        if (rows != 1) {
            throw new IllegalStateException("Failed to adjust balance for account with id: " + account.getId());
        }
        return saved;

    }

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
        t.setPostedDate(null);
        t.setCategoryId(null);
        t.setSource(TransactionSource.OPENING_BALANCE);
        Transaction saved = txRepo.save(t);

        int rows = accountRepo.adjustBalance(accountId, openingBalance);
        if (rows != 1) {
            throw new IllegalStateException("Failed to update account with ID " + accountId + " with an opening balance.");
        }
        return saved;
    }
}
