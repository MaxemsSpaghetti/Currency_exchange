package maxim.butenko.service;

import maxim.butenko.dao.ExchangeRateDAO;
import maxim.butenko.dao.ExchangeRateDAOImpl;
import maxim.butenko.model.CurrencyExchange;
import maxim.butenko.model.ExchangeRate;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CurrencyExchangeService {

    private static final CurrencyExchangeService INSTANCE = new CurrencyExchangeService();
    private final ExchangeRateDAO exchangeRateDAO = ExchangeRateDAOImpl.getInstance();

    private static final DecimalFormat decimalFormatForAmount;

    private static final DecimalFormat decimalFormatForRate;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());

        symbols.setDecimalSeparator('.');

        decimalFormatForAmount = new DecimalFormat("#.##", symbols);

        decimalFormatForRate = new DecimalFormat("#.######", symbols);

    }

    private CurrencyExchangeService() {

    }

    public static CurrencyExchangeService getInstance() {
        return INSTANCE;
    }

    public Optional<CurrencyExchange> convertCurrency(
            String baseCurrencyCode, String targetCurrencyCode, Double amount) throws SQLException {

        Optional<ExchangeRate> currencyPair = exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode);
        if (currencyPair.isPresent()) {
            return directCurrencyExchange(currencyPair, amount);
        }

        Optional<ExchangeRate> ReverseCurrencyPair = exchangeRateDAO.findByCodes(
                targetCurrencyCode, baseCurrencyCode);
        if (ReverseCurrencyPair.isPresent()) {
            return reverseCurrencyExchange(ReverseCurrencyPair, amount);
        }

        List<ExchangeRate> byCodesSeparately = exchangeRateDAO.findByCodesSeparately(
                baseCurrencyCode, targetCurrencyCode);
        if (!byCodesSeparately.isEmpty()) {
            return crossCurrencyExchange(byCodesSeparately, amount);
        }

        return Optional.empty();
    }

    private Optional<CurrencyExchange> directCurrencyExchange(Optional<ExchangeRate> currencyPair, Double amount) {
        ExchangeRate exchangeRate = currencyPair.get();
        Double rate = exchangeRate.getRate();
        return Optional.of(new CurrencyExchange(exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                rate,
                amount,
                Double.parseDouble(decimalFormatForAmount.format(rate * amount))));
    }

    private Optional<CurrencyExchange> reverseCurrencyExchange(Optional<ExchangeRate> currencyPair, Double amount) {
        ExchangeRate exchangeRate = currencyPair.get();
        Double rate = exchangeRate.getRate();
        return Optional.of(new CurrencyExchange(exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                Double.parseDouble(decimalFormatForRate.format(1 / rate)),
                amount,
                Double.parseDouble(decimalFormatForAmount.format(1 / rate * amount))));
    }

    private Optional<CurrencyExchange> crossCurrencyExchange(List<ExchangeRate> currencyPairs, Double amount) {
        ExchangeRate exchangeRate = currencyPairs.get(0);
        Double firstRate = exchangeRate.getRate();
        ExchangeRate secondExchangeRate = currencyPairs.get(1);
        Double secondRate = secondExchangeRate.getRate();

        return Optional.of(new CurrencyExchange(exchangeRate.getTargetCurrency(),
                secondExchangeRate.getTargetCurrency(),
                Double.parseDouble(decimalFormatForRate.format(firstRate / secondRate)),
                amount,
                Double.parseDouble(decimalFormatForAmount.format(firstRate / secondRate * amount))));
    }
}
