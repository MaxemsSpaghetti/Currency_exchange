package maxim.butenko.dao;

import lombok.SneakyThrows;
import maxim.butenko.config.ConnectionManager;
import maxim.butenko.model.Currency;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImpl implements CurrencyDAO {

    private static final CurrencyDAOImpl INSTANCE = new CurrencyDAOImpl();

    private static final String FIND_ALL = "SELECT * FROM currencies";

    private static final String FIND_BY_CODE = "SELECT * FROM currencies WHERE code = ?";

    private CurrencyDAOImpl() {

    }

    @Override
    @SneakyThrows
    public List<Currency> findAll() {
        var connection = ConnectionManager.get();
        var prepareStatement  = connection.prepareStatement(FIND_ALL);
        var resultSet = prepareStatement.executeQuery();
        List<Currency> currencies = new ArrayList<>();
        while (resultSet.next()) {
            currencies.add(buildCurrency(resultSet));
        }
        return currencies;
    }

    @Override
    @SneakyThrows
    public Optional<Currency> findByCode(String code) {
        var connection = ConnectionManager.get();
        var prepareStatement = connection.prepareStatement(FIND_BY_CODE);
        prepareStatement.setString(1, code);
        var result = prepareStatement.executeQuery();

        if (!result.next()) {
            return Optional.empty();
        }
        return Optional.of(buildCurrency(result));
    }


    @Override
    public void update(Currency currency) {

    }

    @Override
    public Currency save(Currency currency) {
        return null;
    }

    public static CurrencyDAOImpl getInstance() {
        return INSTANCE;
    }

    @SneakyThrows
    private Currency buildCurrency(ResultSet resultSet) {
        return new Currency(
            resultSet.getObject("id", Long.class),
                resultSet.getObject("fullName", String.class),
                resultSet.getObject("code", String.class),
                resultSet.getObject("sign", String.class)
        );
    }
}
