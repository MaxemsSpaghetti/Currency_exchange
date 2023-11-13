package maxim.butenko.dao;

import maxim.butenko.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO extends DAO<ExchangeRate> {
    Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;

    List<ExchangeRate> findByCodesSeparately(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;
}
