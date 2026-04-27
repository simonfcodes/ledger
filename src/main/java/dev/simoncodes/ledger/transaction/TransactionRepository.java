package dev.simoncodes.ledger.transaction;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    Optional<Transaction> findByIdAndAccountId(UUID id, UUID accountId);
}
