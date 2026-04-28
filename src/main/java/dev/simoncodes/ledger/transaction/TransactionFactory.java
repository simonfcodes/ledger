package dev.simoncodes.ledger.transaction;

import dev.simoncodes.ledger.account.entity.Account;
import dev.simoncodes.ledger.common.StringUtils;
import dev.simoncodes.ledger.transaction.dto.CreateTransactionRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class TransactionFactory {
    private TransactionFactory() {}

    public static Transaction fromCreateRequest(CreateTransactionRequest req, Account account) {
        Transaction t = new Transaction();
        t.setAccountId(account.getId());
        t.setAmount(req.amount());
        t.setDirection(req.direction());
        t.setCurrencyCode(account.getCurrencyCode());
        t.setTransactionDate(req.transactionDate());
        t.setDescription(StringUtils.nullIfBlank(req.description()));
        t.setReference(StringUtils.nullIfBlank(req.reference()));
        t.setNotes(StringUtils.nullIfBlank(req.notes()));
        t.setPostedDate(req.postedDate());
        t.setCategoryId(req.categoryId());
        t.setSource(TransactionSource.MANUAL);
        t.setOriginalAmount(req.originalAmount() == null ? req.amount() : req.originalAmount());
        t.setOriginalCurrencyCode(req.originalCurrencyCode() == null ? account.getCurrencyCode() : req.originalCurrencyCode());
        t.setRecurring(false);

        return t;
    }

    public static Transaction fromOpeningBalance(UUID accountId, BigDecimal openingBalance, String currencyCode) {
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
        t.setDescription("Opening balance");
        t.setSource(TransactionSource.OPENING_BALANCE);

        return t;
    }
}
