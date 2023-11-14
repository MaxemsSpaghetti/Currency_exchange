package maxim.butenko.dao;

import maxim.butenko.config.ConnectionManager;
import maxim.butenko.model.Currency;
import maxim.butenko.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
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
                resultSet.getObject("rate", BigDecimal.class)
        );
    }

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        try (Connection connection = ConnectionManager.get()){
            PreparedStatement prepareStatement = connection.prepareStatement(SQLQuery.FIND_ALL.QUERY);
            ResultSet resultSet = prepareStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        }
    }


    @Override
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        try (Connection connection = ConnectionManager.get()){
            PreparedStatement prepareStatement = connection.prepareStatement(SQLQuery.FIND_BY_CODES.QUERY);
            prepareStatement.setString(1, baseCurrencyCode);
            prepareStatement.setString(2, targetCurrencyCode);
            ResultSet result = prepareStatement.executeQuery();

            if (!result.next()) {
                return Optional.empty();
            }
            return Optional.of(buildExchangeRate(result));
        }
    }
    @Override
    public List<ExchangeRate> findByCodesSeparately(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        try (Connection connection = ConnectionManager.get()) {
            PreparedStatement prepareStatement = connection.prepareStatement(SQLQuery.FIND_BY_CODES_SEPARATELY.QUERY);
            prepareStatement.setString(1, baseCurrencyCode);
            prepareStatement.setString(2, targetCurrencyCode);
            ResultSet result = prepareStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (result.next()) {
                exchangeRates.add(buildExchangeRate(result));
            }
            return exchangeRates;
        }
    }

    @Override
    public ExchangeRate update(ExchangeRate exchangeRate) throws SQLException {
        try (Connection connection = ConnectionManager.get()){
            PreparedStatement prepareStatement = connection.prepareStatement(SQLQuery.UPDATE.QUERY);
            prepareStatement.setObject(1, exchangeRate.getRate());
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
        try (Connection connection = ConnectionManager.get()) {
            connection.setAutoCommit(false);
            PreparedStatement prepareStatement = connection.prepareStatement(SQLQuery.CREATE.QUERY, Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setObject(1, exchangeRate.getBaseCurrency().getId());
            prepareStatement.setObject(2, exchangeRate.getTargetCurrency().getId());
            prepareStatement.setObject(3, exchangeRate.getRate());

            prepareStatement.execute();

            ResultSet newExchangeRate = prepareStatement.getGeneratedKeys();
            newExchangeRate.next();
            exchangeRate.setId(newExchangeRate.getLong(1));
            connection.commit();
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

        FIND_BY_CODES("""
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

        FIND_BY_CODES_SEPARATELY("""
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
                    baseCurrencyId = (SELECT currency.id FROM currencies currency WHERE currency.code = 'USD') AND
                    (targetCurrencyId = (SELECT currency1.id FROM currencies currency1 WHERE currency1.code = ?) OR
                    targetCurrencyId = (SELECT currency2.id FROM currencies currency2 WHERE currency2.code = ?))
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
