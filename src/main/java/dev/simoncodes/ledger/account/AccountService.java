package dev.simoncodes.ledger.account;

import dev.simoncodes.ledger.account.dto.CreateAccountRequest;
import dev.simoncodes.ledger.account.entity.Account;
import dev.simoncodes.ledger.account.view.AccountView;
import dev.simoncodes.ledger.common.exception.ResourceNotFoundException;
import dev.simoncodes.ledger.currency.Currency;
import dev.simoncodes.ledger.currency.CurrencyRepository;
import dev.simoncodes.ledger.institution.Institution;
import dev.simoncodes.ledger.institution.InstitutionRepository;
import dev.simoncodes.ledger.transaction.TransactionService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepo;
    private final InstitutionRepository institutionRepo;
    private final CurrencyRepository currencyRepo;
    private final TransactionService transactionService;

    public AccountView getAccount(UUID userId, UUID accountId) {
        Account account = accountRepo.findById(accountId)
                .filter(a -> a.getUserId().equals(userId))
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
        transactionService.createOpeningBalance(savedAcct.getId(), request.openingBalance(), request.currencyCode());
        Account finalAcct = accountRepo.findById(savedAcct.getId()).orElseThrow(() -> new IllegalStateException("Failed to retrieve account that should have already been created and saved to the database with id: " + savedAcct.getId()));
        return AccountMapper.toView(finalAcct);
    }
}
