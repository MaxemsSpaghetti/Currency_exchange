package maxim.butenko.dao;

import maxim.butenko.config.ConnectionManager;
import maxim.butenko.model.Currency;
import maxim.butenko.model.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImpl implements ExchangeRateDAO {

    private static final ExchangeRateDAOImpl INSTANCE = new ExchangeRateDAOImpl();

    private ExchangeRateDAOImpl() {

    }

    public static ExchangeRateDAOImpl getInstance() {
        return INSTANCE;
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getObject("id", Long.class),
                new Currency(
                        resultSet.getObject("base_id", Long.class),
                        resultSet.getObject("base_fullName", String.class),
                        resultSet.getObject("base_code", String.class),
                        resultSet.getObject("base_sign", String.class)
                ),
                new Currency(
                        resultSet.getObject("target_id", Long.class),
                        resultSet.getObject("target_fullName", String.class),
                        resultSet.getObject("target_code", String.class),
                        resultSet.getObject("target_sign", String.class)
                ),
                resultSet.getDouble("rate")
        );
    }

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        try (var connection = ConnectionManager.get()){
            var prepareStatement = connection.prepareStatement(SQLQuery.FIND_ALL.QUERY);
            var resultSet = prepareStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        }
    }


    @Override
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        try (var connection = ConnectionManager.get()){
            var prepareStatement = connection.prepareStatement(SQLQuery.FIND_BY_CODE.QUERY);
            prepareStatement.setString(1, baseCurrencyCode);
            prepareStatement.setString(2, targetCurrencyCode);
            var result = prepareStatement.executeQuery();

            if (!result.next()) {
                return Optional.empty();
            }
            return Optional.of(buildExchangeRate(result));
        }
    }

    @Override
    public ExchangeRate update(ExchangeRate exchangeRate) throws SQLException {
        try (var connection = ConnectionManager.get()){
            var prepareStatement = connection.prepareStatement(SQLQuery.UPDATE.QUERY);
            prepareStatement.setDouble(1, exchangeRate.getRate());
            prepareStatement.setObject(2, exchangeRate.getBaseCurrency().getId());
            prepareStatement.setObject(3, exchangeRate.getTargetCurrency().getId());
            var executeUpdate = prepareStatement.executeUpdate();
            if (executeUpdate == 0) {
                return null;
            }
        }
        return exchangeRate;
    }
    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) throws SQLException {

        try (var connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            var prepareStatement = connection.prepareStatement(SQLQuery.CREATE.QUERY, Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setObject(1, exchangeRate.getBaseCurrency().getId());
            prepareStatement.setObject(2, exchangeRate.getTargetCurrency().getId());
            prepareStatement.setDouble(3, exchangeRate.getRate());

            prepareStatement.execute();

            var newExchangeRate = prepareStatement.getGeneratedKeys();
            newExchangeRate.next();
            exchangeRate.setId(newExchangeRate.getLong(1));
            connection.commit(); // Завершить транзакцию
            connection.setAutoCommit(true);
        }
        return exchangeRate;
    }

    enum SQLQuery {
        FIND_ALL("""
                SELECT
                    exchangeRate.id AS id,
                    baseCurrency.id AS base_id,
                    baseCurrency.fullName AS base_fullName,
                    baseCurrency.code AS base_code,
                    baseCurrency.sign AS base_sign,
                    targetCurrency.id AS target_id,
                    targetCurrency.fullName AS target_fullName,
                    targetCurrency.code AS target_code,
                    targetCurrency.sign AS target_sign,
                    exchangeRate.rate AS rate
                FROM exchangeRates exchangeRate
                JOIN currencies baseCurrency ON exchangeRate.baseCurrencyId = baseCurrency.id
                JOIN currencies targetCurrency ON exchangeRate.targetCurrencyId = targetCurrency.id
                ORDER BY exchangeRate.id
            """),

        FIND_BY_CODE("""
                SELECT
                exchangeRate.id AS id,
                baseCurrency.id AS base_id,
                baseCurrency.fullName AS base_fullName,
                baseCurrency.code AS base_code,
                baseCurrency.sign AS base_sign,
                targetCurrency.id AS target_id,
                targetCurrency.fullName AS target_fullName,
                targetCurrency.code AS target_code,
                targetCurrency.sign AS target_sign,
                exchangeRate.rate AS rate
                FROM exchangeRates exchangeRate
                JOIN currencies baseCurrency ON exchangeRate.baseCurrencyId = baseCurrency.id
                JOIN currencies targetCurrency ON exchangeRate.targetCurrencyId = targetCurrency.id
                WHERE (
                    baseCurrencyId = (SELECT currency.id FROM currencies currency WHERE currency.code = ?) AND
                    targetCurrencyId = (SELECT currency.id FROM currencies currency WHERE currency.code = ?)   
                )    
            """),

        CREATE("INSERT INTO exchangeRates (baseCurrencyId, targetCurrencyId, rate) VALUES (?, ?, ?)"),

        UPDATE("UPDATE exchangeRates SET rate = ? WHERE (baseCurrencyId = ? AND targetCurrencyId = ?)");
        String QUERY;

        SQLQuery(String query) {
            this.QUERY = query;
        }
    }
}
