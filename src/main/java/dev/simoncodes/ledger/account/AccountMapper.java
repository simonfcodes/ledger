package dev.simoncodes.ledger.account;

import dev.simoncodes.ledger.account.dto.*;
import dev.simoncodes.ledger.account.entity.*;
import dev.simoncodes.ledger.account.view.*;

import java.math.BigDecimal;
import java.util.UUID;

class AccountMapper {

    static Account toEntity(UUID userId, CreateAccountRequest request) {
        Account a = new Account();
        a.setUserId(userId);
        a.setInstitutionId(request.institutionId());
        a.setName(request.name());
        a.setCurrencyCode(request.currencyCode());
        a.setCountryCode(request.countryCode());
        a.setCurrentBalance(BigDecimal.ZERO);
        a.setConnectionType(ConnectionType.MANUAL);
        a.setActive(true);

        switch (request) {
            case CreateCurrentAccountRequest r -> {
                a.setType(AccountType.CURRENT);
                CurrentAccountDetails d = new CurrentAccountDetails();
                d.setOverdraftLimit(r.overdraftLimit() == null ? BigDecimal.ZERO : r.overdraftLimit());
                a.setCurrentAccountDetails(d);
            }
            case CreateSavingsAccountRequest r -> {
                a.setType(AccountType.SAVINGS);
                SavingsDetails d = new SavingsDetails();
                d.setInterestRate(r.interestRate() == null ? BigDecimal.ZERO : r.interestRate());
                a.setSavingsDetails(d);
            }
            case CreateCreditCardAccountRequest r -> {
                a.setType(AccountType.CREDIT_CARD);
                CreditCardDetails d = new CreditCardDetails();
                d.setCreditLimit(r.creditLimit());
                d.setApr(r.apr() == null ? BigDecimal.ZERO : r.apr());
                d.setLastStatementBalance(r.lastStatementBalance() == null ? BigDecimal.ZERO : r.lastStatementBalance());
                d.setLastStatementDate(r.lastStatementDate());
                d.setNextPaymentDueDate(r.nextPaymentDueDate());
                d.setNextPaymentAmount(r.nextPaymentAmount() == null ? BigDecimal.ZERO : r.nextPaymentAmount());
                a.setCreditCardDetails(d);
            }
            case CreateLoanAccountRequest r -> {
                a.setType(AccountType.LOAN);
                LoanDetails d = new LoanDetails();
                d.setLoanAmount(r.loanAmount());
                d.setInterestRate(r.interestRate() == null ? BigDecimal.ZERO : r.interestRate());
                d.setTermMonths(r.termMonths() == null ? 0 : r.termMonths());
                d.setMonthlyPayment(r.monthlyPayment() == null ? BigDecimal.ZERO : r.monthlyPayment());
                a.setLoanDetails(d);
            }
        }
        return a;
    }
    static AccountView toView(Account account) {
        return switch (account.getType()) {
            case CURRENT -> new CurrentAccountView(
                    account.getId(),
                    account.getUserId(),
                    account.getInstitutionId(),
                    account.getName(),
                    account.getType(),
                    account.getCurrencyCode(),
                    account.getCountryCode(),
                    account.getCurrentBalance(),
                    account.getDisplayOrder(),
                    account.getConnectionType(),
                    account.isActive(),
                    account.getCreatedAt(),
                    account.getUpdatedAt(),
                    requireDetails(account.getCurrentAccountDetails(), account.getType(), account.getId())
            );
            case SAVINGS -> new SavingsAccountView(
                    account.getId(),
                    account.getUserId(),
                    account.getInstitutionId(),
                    account.getName(),
                    account.getType(),
                    account.getCurrencyCode(),
                    account.getCountryCode(),
                    account.getCurrentBalance(),
                    account.getDisplayOrder(),
                    account.getConnectionType(),
                    account.isActive(),
                    account.getCreatedAt(),
                    account.getUpdatedAt(),
                    requireDetails(account.getSavingsDetails(), account.getType(), account.getId())
            );
            case CREDIT_CARD -> new CreditCardAccountView(
                    account.getId(),
                    account.getUserId(),
                    account.getInstitutionId(),
                    account.getName(),
                    account.getType(),
                    account.getCurrencyCode(),
                    account.getCountryCode(),
                    account.getCurrentBalance(),
                    account.getDisplayOrder(),
                    account.getConnectionType(),
                    account.isActive(),
                    account.getCreatedAt(),
                    account.getUpdatedAt(),
                    requireDetails(account.getCreditCardDetails(), account.getType(), account.getId())
            );
            case LOAN -> new LoanAccountView(
                    account.getId(),
                    account.getUserId(),
                    account.getInstitutionId(),
                    account.getName(),
                    account.getType(),
                    account.getCurrencyCode(),
                    account.getCountryCode(),
                    account.getCurrentBalance(),
                    account.getDisplayOrder(),
                    account.getConnectionType(),
                    account.isActive(),
                    account.getCreatedAt(),
                    account.getUpdatedAt(),
                    requireDetails(account.getLoanDetails(), account.getType(), account.getId())
            );
        };
    }

    private static <T> T requireDetails(T details, AccountType type, UUID id) {
        if (details == null) {
            throw new IllegalStateException("Account with ID " + id + " has type " + type + " but no associated details.");
        }
        return details;
    }
}
