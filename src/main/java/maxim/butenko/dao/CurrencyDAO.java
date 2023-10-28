package maxim.butenko.dao;

import maxim.butenko.model.Currency;

import java.util.Optional;

public interface CurrencyDAO extends DAO<Currency> {

    Optional<Currency> findByCode(String code);
}
