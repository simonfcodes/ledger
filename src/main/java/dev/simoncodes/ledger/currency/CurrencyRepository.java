package dev.simoncodes.ledger.currency;

import dev.simoncodes.ledger.common.ReadOnlyRepository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends ReadOnlyRepository<Currency, String> {

    Optional<Currency> findByCode(String s);

    List<Currency> findAll();
}
