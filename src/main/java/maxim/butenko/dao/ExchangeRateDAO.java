package maxim.butenko.dao;

import maxim.butenko.model.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDAO extends DAO<ExchangeRate> {
    Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode);
}
