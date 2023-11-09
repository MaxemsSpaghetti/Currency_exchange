package maxim.butenko.dao;

import maxim.butenko.config.ConnectionManager;
import maxim.butenko.model.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImpl implements CurrencyDAO {

    private static final CurrencyDAOImpl INSTANCE = new CurrencyDAOImpl();

    private CurrencyDAOImpl() {

    }

    public static CurrencyDAOImpl getInstance() {
        return INSTANCE;
    }


    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("fullName"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }

    @Override
    public List<Currency> findAll() throws SQLException {
        try (var connection = ConnectionManager.get()){
            var prepareStatement = connection.prepareStatement(SQLQuery.FIND_ALL.QUERY);
            var resultSet = prepareStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) throws SQLException {
        try (var connection = ConnectionManager.get()) {
            var prepareStatement = connection.prepareStatement(SQLQuery.FIND_BY_CODE.QUERY);

            prepareStatement.setString(1, code);
            var result = prepareStatement.executeQuery();

            if (!result.next()) {
                return Optional.empty();
            }
            return Optional.of(buildCurrency(result));
        }
    }


    @Override
    public Currency update(Currency currency) {
        return null;
    }

    @Override
    public Currency save(Currency currency) throws SQLException {
        try (var connection = ConnectionManager.get()){
            var prepareStatement = connection.prepareStatement(SQLQuery.CREATE.QUERY);
            prepareStatement.setString(1, currency.getFullName());
            prepareStatement.setString(2, currency.getCode());
            prepareStatement.setString(3, currency.getSign());
            var executeUpdate = prepareStatement.executeUpdate();
            if (executeUpdate == 0) {
                return null;
            }
            try (var generatedKeys = prepareStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    var generatedId = generatedKeys.getLong(1);
                    currency.setId(generatedId);
                } else {
                    return null;
                }
            }
            return currency;
        }
    }

    enum SQLQuery {
        FIND_ALL("SELECT * FROM currencies"),
        FIND_BY_CODE("SELECT * FROM currencies WHERE code = ?"),
        CREATE("INSERT INTO currencies (fullName, code, sign) VALUES (?, ?, ?)");

        String QUERY;

        SQLQuery(String query) {
            this.QUERY = query;
        }
    }
}
