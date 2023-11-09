package maxim.butenko.dao;

import maxim.butenko.model.Currency;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyDAO extends DAO<Currency> {

    Optional<Currency> findByCode(String code) throws SQLException;
}
