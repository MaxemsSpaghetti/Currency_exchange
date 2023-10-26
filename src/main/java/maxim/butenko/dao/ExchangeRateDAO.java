package maxim.butenko.dao;

import maxim.butenko.config.ConnectionManager;
import maxim.butenko.model.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO implements DAO<Long, ExchangeRate> {

    private static final ExchangeRateDAO INSTANCE = new ExchangeRateDAO();

    private ExchangeRateDAO() {

    }

    public static ExchangeRateDAO getInstance() {
        return INSTANCE;
    }

    public static final String FIND_ALL_BY_CURRENCY = "SELECT * FROM ExchangeRates where baseCurrencyId = ?";

    @Override
    public List<ExchangeRate> findAll() {
        return null;
    }

    public List<ExchangeRate> findByCurrencyId(Long CurrencyId) {
        try (var connection = ConnectionManager.get();
        var preparedStatement = connection.prepareStatement(FIND_ALL_BY_CURRENCY)) {
            preparedStatement.setObject(1, CurrencyId);
            var resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
                return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getObject("id", Long.class),
                resultSet.getObject("base_currency_id", Integer.class),
                resultSet.getObject("target_currency_id", Integer.class),
                resultSet.getObject("rate", Double.class)
        );
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public void update(ExchangeRate exchangeRate) {

    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        return null;
    }
}
