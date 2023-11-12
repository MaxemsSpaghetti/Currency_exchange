package maxim.butenko.service;

import maxim.butenko.dao.ExchangeRateDAOImpl;
import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.dto.ExchangeRateDTO;
import maxim.butenko.model.Currency;
import maxim.butenko.model.CurrencyExchange;
import maxim.butenko.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ExchangeRateService {

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRateDAOImpl exchangeRateDAO = ExchangeRateDAOImpl.getInstance();

    private final CurrencyService currencyService = CurrencyService.getInstance();

    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    private ExchangeRateService() {

    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateDTO> findAll() throws SQLException {
        return exchangeRateDAO.findAll().stream()
                .map(this::buildExchangeRate)
                .collect(toList());
    }

    public Optional<ExchangeRateDTO> findByCodes(
            String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode).stream()
                .map(this::buildExchangeRate)
                .findFirst();
    }

    public Optional<ExchangeRateDTO> create(
            String baseCurrencyCode, String targetCurrencyCode, Double rate) throws SQLException {
        CurrencyDTO baseCurrencyDTO = currencyService.findByCode(baseCurrencyCode).orElseThrow();
        CurrencyDTO targetCurrencyDTO = currencyService.findByCode(targetCurrencyCode).orElseThrow();

        Currency baseCurrency = currencyMapper.convertCurrencyDTOToCurrency(baseCurrencyDTO);
        Currency targetCurrency = currencyMapper.convertCurrencyDTOToCurrency(targetCurrencyDTO);

        ExchangeRate save = exchangeRateDAO.save(new ExchangeRate(null, baseCurrency, targetCurrency, rate));

        return Optional.ofNullable(buildExchangeRate(save));
    }

    public Optional<ExchangeRateDTO> update(
            String baseCurrencyCode, String targetCurrencyCode, Double rate) throws SQLException {
        Optional<ExchangeRate> rateBeforeUpdate = exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode);

        if (rateBeforeUpdate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate exchangeRate = rateBeforeUpdate.get();
        exchangeRate.setRate(rate);
        ExchangeRate update = exchangeRateDAO.update(exchangeRate);

        return Optional.ofNullable(buildExchangeRate(update));
    }

    private ExchangeRateDTO buildExchangeRate(ExchangeRate exchangeRate) {
        CurrencyDTO baseCurrency = currencyMapper.convertCurrencyToCurrencyDTO(exchangeRate.getBaseCurrency());
        CurrencyDTO targetCurrency = currencyMapper.convertCurrencyToCurrencyDTO(exchangeRate.getTargetCurrency());

        return ExchangeRateDTO.builder()
                .id(exchangeRate.getId())
                .baseCurrencyId(baseCurrency)
                .targetCurrencyId(targetCurrency)
                .rate(exchangeRate.getRate())
                .build();
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
                rate * amount));
    }

    private Optional<CurrencyExchange> reverseCurrencyExchange(Optional<ExchangeRate> currencyPair, Double amount) {
        ExchangeRate exchangeRate = currencyPair.get();
        Double rate = exchangeRate.getRate();
        return Optional.of(new CurrencyExchange(exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                1 / rate,
                amount,
                1 / rate * amount));
    }

    private Optional<CurrencyExchange> crossCurrencyExchange(List<ExchangeRate> currencyPairs, Double amount) {
        ExchangeRate exchangeRate = currencyPairs.get(0);
        Double firstRate = exchangeRate.getRate();
        ExchangeRate secondExchangeRate = currencyPairs.get(1);
        Double secondRate = secondExchangeRate.getRate();

        return Optional.of(new CurrencyExchange(exchangeRate.getTargetCurrency(),
                secondExchangeRate.getTargetCurrency(),
                firstRate / secondRate, amount,
                firstRate / secondRate * amount));
    }
}