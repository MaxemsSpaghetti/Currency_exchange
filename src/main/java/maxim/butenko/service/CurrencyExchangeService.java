package maxim.butenko.service;

import maxim.butenko.dao.ExchangeRateDAO;
import maxim.butenko.dao.ExchangeRateDAOImpl;
import maxim.butenko.model.CurrencyExchange;
import maxim.butenko.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyExchangeService {

    private static final CurrencyExchangeService INSTANCE = new CurrencyExchangeService();
    private final ExchangeRateDAO exchangeRateDAO = ExchangeRateDAOImpl.getInstance();

    private CurrencyExchangeService() {

    }

    public static CurrencyExchangeService getInstance() {
        return INSTANCE;
    }

    public Optional<CurrencyExchange> convertCurrency(
            String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {

        Optional<ExchangeRate> currencyPair = exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode);
        if (currencyPair.isPresent()) {
            return directCurrencyExchange(currencyPair.get(), amount);
        }

        Optional<ExchangeRate> ReverseCurrencyPair = exchangeRateDAO.findByCodes(
                targetCurrencyCode, baseCurrencyCode);
        if (ReverseCurrencyPair.isPresent()) {
            return reverseCurrencyExchange(ReverseCurrencyPair.get(), amount);
        }

        List<ExchangeRate> byCodesSeparately = exchangeRateDAO.findByCodesSeparately(
                baseCurrencyCode, targetCurrencyCode);
        if (!byCodesSeparately.isEmpty()) {
            return crossCurrencyExchange(byCodesSeparately, amount);
        }

        return Optional.empty();
    }

    private Optional<CurrencyExchange> directCurrencyExchange(ExchangeRate exchangeRate, BigDecimal amount) {
        BigDecimal rate = exchangeRate.getRate();

        BigDecimal convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);

        return Optional.of(new CurrencyExchange(exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                rate,
                amount,
                convertedAmount));
    }

    private Optional<CurrencyExchange> reverseCurrencyExchange(ExchangeRate exchangeRate, BigDecimal amount) {
        BigDecimal rate = exchangeRate.getRate();

        BigDecimal finalRate = BigDecimal.ONE.divide(rate).setScale(6, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = BigDecimal.ONE.divide(rate.multiply(amount))
                .setScale(2, RoundingMode.HALF_UP);

        return Optional.of(new CurrencyExchange(exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                finalRate,
                amount,
                convertedAmount));
    }

    private Optional<CurrencyExchange> crossCurrencyExchange(List<ExchangeRate> currencyPairs, BigDecimal amount) {
        ExchangeRate exchangeRate = currencyPairs.get(0);
        BigDecimal firstRate = exchangeRate.getRate();
        ExchangeRate secondExchangeRate = currencyPairs.get(1);
        BigDecimal secondRate = secondExchangeRate.getRate();

        BigDecimal finalRate = firstRate.divide(secondRate.setScale(6, RoundingMode.HALF_UP));
        BigDecimal convertedAmount = finalRate.divide(secondRate.multiply(amount)
                .setScale(2, RoundingMode.HALF_UP));

        return Optional.of(new CurrencyExchange(exchangeRate.getTargetCurrency(),
                secondExchangeRate.getTargetCurrency(),
                finalRate,
                amount,
                convertedAmount));
    }
}
