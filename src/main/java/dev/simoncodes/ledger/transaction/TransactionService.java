package dev.simoncodes.ledger.transaction;

import dev.simoncodes.ledger.account.AccountRepository;
import dev.simoncodes.ledger.account.entity.Account;
import dev.simoncodes.ledger.category.CategoryRepository;
import dev.simoncodes.ledger.common.StringUtils;
import dev.simoncodes.ledger.common.exception.ConflictException;
import dev.simoncodes.ledger.common.exception.ResourceNotFoundException;
import dev.simoncodes.ledger.currency.CurrencyRepository;
import dev.simoncodes.ledger.transaction.dto.CreateTransactionRequest;
import dev.simoncodes.ledger.transaction.dto.TransactionListRequest;
import dev.simoncodes.ledger.transaction.dto.UpdateTransactionRequest;
import dev.simoncodes.ledger.transaction.pagination.CursorCodec;
import dev.simoncodes.ledger.transaction.pagination.TransactionCursor;
import dev.simoncodes.ledger.transaction.view.TransactionListView;
import dev.simoncodes.ledger.transaction.view.TransactionView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository txRepo;
    private final AccountRepository accountRepo;
    private final CurrencyRepository currencyRepo;
    private final CategoryRepository categoryRepo;
    private final CursorCodec cursorCodec;

    public TransactionListView listTransactions(UUID userId, UUID accountId, TransactionListRequest req) {
        verifyAccountOwnership(userId, accountId);

        TransactionCursor cursor = req.cursor() == null ? null : cursorCodec.decode(req.cursor());
        LocalDate cursorDate = cursor != null ? cursor.transactionDate() : null;
        Instant cursorCreatedAt = cursor != null ? cursor.createdAt() : null;
        UUID cursorId = cursor != null ? cursor.id() : null;

        List<Transaction> rows = txRepo.findPage(
                accountId,
                cursorDate, cursorCreatedAt, cursorId,
                req.fromDate(), req.toDate(), req.direction(), req.categoryId(),
                req.size() + 1
        );

        boolean hasMore = rows.size() > req.size();
        List<Transaction> page = hasMore ? rows.subList(0, req.size()) : rows;

        String nextCursor = null;

        if (hasMore) {
            Transaction last = page.getLast();
            nextCursor = cursorCodec.encode(new TransactionCursor(
                    last.getTransactionDate(),
                    last.getCreatedAt(),
                    last.getId()
            ));
        }

        log.info("Listed {} transactions for account {} (hasMore={})", page.size(), accountId, hasMore);

        return new TransactionListView(
                page.stream().map(TransactionView::fromTransaction).toList(),
                nextCursor,
                hasMore
        );
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(UUID userId, UUID accountId, UUID txId) {
        verifyAccountOwnership(userId, accountId);
        return txRepo.findByIdAndAccountId(txId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + txId));
    }

    @Transactional
    public Transaction createTransaction(UUID userId, UUID accountId, CreateTransactionRequest req) {
        Account account = loadAccountAndValidate(userId, accountId);
        validateCategoryAndCurrency(userId, req.categoryId(), req.originalCurrencyCode());
        Transaction t = TransactionFactory.fromCreateRequest(req, account);
        Transaction saved = persistWithBalanceDelta(t, t.signedAmount());
        log.info("Created new transaction with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Transaction createOpeningBalance(UUID accountId, BigDecimal openingBalance, String currencyCode) {
        Transaction t = TransactionFactory.fromOpeningBalance(accountId, openingBalance, currencyCode);
        Transaction saved = persistWithBalanceDelta(t, openingBalance);
        log.info("Created opening balance transaction with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Transaction updateTransaction(UUID userId, UUID accountId, UUID txId, UpdateTransactionRequest req) {
        Account account = loadAccountAndValidate(userId, accountId);
        Transaction tx = txRepo.findByIdAndAccountId(txId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + txId));
        // TODO: loosen to per-field check
        //       — description/notes/category should be editable on opening balance

        if (tx.getSource() == TransactionSource.OPENING_BALANCE) {
            throw new ConflictException("Cannot update an opening balance transaction: " + txId);
        }
        validateCategoryAndCurrency(userId, req.categoryId(), req.originalCurrencyCode());
        BigDecimal oldSignedAmount = tx.signedAmount();

        tx.setAmount(req.amount());
        tx.setDirection(req.direction());
        tx.setTransactionDate(req.transactionDate());
        tx.setDescription(StringUtils.nullIfBlank(req.description()));
        tx.setReference(StringUtils.nullIfBlank(req.reference()));
        tx.setNotes(StringUtils.nullIfBlank(req.notes()));
        tx.setPostedDate(req.postedDate());
        tx.setCategoryId(req.categoryId());
        tx.setOriginalAmount(req.originalAmount() == null ? req.amount() : req.originalAmount());
        tx.setOriginalCurrencyCode(req.originalCurrencyCode() == null ? account.getCurrencyCode() : req.originalCurrencyCode());

        BigDecimal delta = tx.signedAmount().subtract(oldSignedAmount);
        Transaction saved = persistWithBalanceDelta(tx, delta);
        log.info("Updated transaction with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public void deleteTransaction(UUID userId, UUID accountId, UUID txId) {
        loadAccountAndValidate(userId, accountId);
        Transaction tx = txRepo.findByIdAndAccountId(txId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + txId));
        if (tx.getSource() == TransactionSource.OPENING_BALANCE) {
            throw new ConflictException("Cannot delete an opening balance transaction: " + txId);
        }
        BigDecimal reversal = tx.signedAmount().negate();
        txRepo.deleteById(txId);
        int rows = accountRepo.adjustBalance(accountId, reversal);
        if (rows != 1) {
            log.warn("Failed to adjust balance for account with id: {}", accountId);
            throw new IllegalStateException("Failed to adjust balance for account with id: " + accountId);
        }
        log.info("Deleted transaction with id: {}", txId);
    }

    private void validateCategoryAndCurrency(UUID userId, UUID categoryId, String originalCurrencyCode) {
        if (categoryId != null && !categoryRepo.existsByIdAndUserId(categoryId, userId)) {
            log.warn("Attempted transaction with unsupported category id: {}", categoryId);
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        if (originalCurrencyCode != null && !currencyRepo.existsById(originalCurrencyCode)) {
            log.warn("Attempted transaction with unsupported currency code: {}", originalCurrencyCode);
            throw new ResourceNotFoundException("Currency not found with id: " + originalCurrencyCode);
        }
    }

    private Account loadAccountAndValidate(UUID userId, UUID accountId) {
        Account account = accountRepo.findById(accountId)
                .filter(a -> a. getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        if (!account.isActive()) {
            log.warn("Attempted transaction on an inactive account with id: {}", account.getId());
            throw new ConflictException("Account is inactive: " + account.getId());
        }
        return account;
    }

    private void verifyAccountOwnership(UUID userId, UUID accountId) {
        if (!accountRepo.existsByIdAndUserId(accountId, userId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }
    }

    private Transaction persistWithBalanceDelta(Transaction t, BigDecimal delta) {
        Transaction saved = txRepo.save(t);
        int rows = accountRepo.adjustBalance(t.getAccountId(), delta);
        if (rows != 1) {
            log.warn("Failed to adjust balance for account with id: {}", t.getAccountId());
            throw new IllegalStateException("Failed to adjust balance for account with id: " + t.getAccountId());
        }
        return saved;
    }
}
