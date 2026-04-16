package dev.simoncodes.ledger.currency;

import dev.simoncodes.ledger.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepo;

    public Currency getCurrency(String code) {
        return currencyRepo.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency with code " + code + " not found"));
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepo.findAll();
    }
}
