package dev.simoncodes.ledger.account;

import dev.simoncodes.ledger.account.dto.CreateAccountRequest;
import dev.simoncodes.ledger.account.dto.UpdateAccountRequest;
import dev.simoncodes.ledger.account.entity.Account;
import dev.simoncodes.ledger.account.view.AccountView;
import dev.simoncodes.ledger.common.exception.ResourceNotFoundException;
import dev.simoncodes.ledger.currency.CurrencyRepository;
import dev.simoncodes.ledger.institution.InstitutionRepository;
import dev.simoncodes.ledger.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepo;
    private final InstitutionRepository institutionRepo;
    private final CurrencyRepository currencyRepo;
    private final TransactionService transactionService;
    private final JdbcTemplate jdbcTemplate;

    public AccountView getAccount(UUID userId, UUID accountId) {
        Account account = accountRepo.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        return AccountMapper.toView(account);
    }

    public List<AccountView> listAccounts(UUID userId) {
        List<Account> accounts = accountRepo.findByUserIdOrderByDisplayOrder(userId);
        return accounts.stream().map(AccountMapper::toView).toList();
    }

    @Transactional
    public AccountView createAccount(UUID userId, CreateAccountRequest request) {
        if (!currencyRepo.existsById(request.currencyCode())) {
            throw new IllegalArgumentException("Currency code not recognized: " + request.currencyCode());
        }
        if (!institutionRepo.existsById(request.institutionId())) {
            throw new IllegalArgumentException("Institution id not recognized: " + request.institutionId());
        }
        Integer maxOrder = accountRepo.findMaxDisplayOrderByUserId(userId);
        int displayOrder = (maxOrder == null) ? 0 : maxOrder + 1;
        Account newAcct = AccountMapper.toEntity(userId, request);
        newAcct.setDisplayOrder(displayOrder);
        Account savedAcct = accountRepo.save(newAcct);
        log.info("Created {} account for user: {} with id: {}", request.currencyCode(), userId, savedAcct.getId());
        transactionService.createOpeningBalance(savedAcct.getId(), request.openingBalance(), request.currencyCode());
        log.info("Set opening balance at {} for account with id: {}", request.openingBalance().toString(), savedAcct.getId());
        Account finalAcct = accountRepo.findById(savedAcct.getId()).orElseThrow(() -> new IllegalStateException("Failed to retrieve account that should have already been created and saved to the database with id: " + savedAcct.getId()));
        return AccountMapper.toView(finalAcct);
    }

    @Transactional
    public AccountView updateAccount(UUID userId, UUID accountId, UpdateAccountRequest request) {
        Account account = accountRepo.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        AccountMapper.applyUpdate(account, request);
        return AccountMapper.toView(accountRepo.save(account));
    }

    @Transactional
    public void deleteAccount(UUID userId, UUID accountId) {
        // Enable bypass of opening balance immutability trigger
        jdbcTemplate.execute("SET LOCAL app.allow_opening_balance_cascade_delete = 'true'");
        int rows = accountRepo.deleteByIdAndUserId(accountId, userId);
        if (rows != 1) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }
        log.info("Deleted account with id: {}", accountId);
    }
}
