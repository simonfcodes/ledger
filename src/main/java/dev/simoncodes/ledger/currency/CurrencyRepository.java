package dev.simoncodes.ledger.currency;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends Repository<Currency, String> {

    Optional<Currency> findByCode(String s);

    List<Currency> findAll();
}
