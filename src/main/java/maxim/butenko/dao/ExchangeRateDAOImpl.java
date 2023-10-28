//package maxim.butenko.dao;
//
//import lombok.SneakyThrows;
//import maxim.butenko.config.ConnectionManager;
//import maxim.butenko.model.ExchangeRate;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class ExchangeRateDAOImpl implements ExchangeRateDAO {
//
//    private static final ExchangeRateDAOImpl INSTANCE = new ExchangeRateDAOImpl();
//
//    private ExchangeRateDAOImpl() {
//
//    }
//
//    public static ExchangeRateDAOImpl getInstance() {
//        return INSTANCE;
//    }
//
//    public static final String FIND_ALL_BY_CURRENCY = "SELECT * FROM ExchangeRates where baseCurrencyId = ?";
//    @Override
//    public List<ExchangeRate> findAll() {
//        return null;
//    }
//    @SneakyThrows
//    public List<ExchangeRate> findByCurrencyId(Long CurrencyId) {
//        try (var connection = ConnectionManager.get();
//            var preparedStatement = connection.prepareStatement(FIND_ALL_BY_CURRENCY)) {
//            preparedStatement.setObject(1, CurrencyId);
//
//            var resultSet = preparedStatement.executeQuery();
//            List<ExchangeRate> exchangeRates = new ArrayList<>();
//            while (resultSet.next()) {
//                exchangeRates.add(buildExchangeRate(resultSet));
//            }
//
//            return exchangeRates;
//        }
//    }
//
//    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
//        return new ExchangeRate(
//                resultSet.getObject("id", Long.class),
//                resultSet.getObject("base_currency_id", Integer.class),
//                resultSet.getObject("target_currency_id", Integer.class),
//                resultSet.getObject("rate", Double.class)
//        );
//    }
//
//
//    public Optional<ExchangeRate> findByCode(String code) {
//        return Optional.empty();
//    }
//
//    @Override
//    public void update(ExchangeRate exchangeRate) {
//
//    }
//    @Override
//    public ExchangeRate save(ExchangeRate exchangeRate) {
//        return null;
//    }
//}
