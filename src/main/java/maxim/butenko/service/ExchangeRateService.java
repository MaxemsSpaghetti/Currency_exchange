package maxim.butenko.service;

import maxim.butenko.dao.ExchangeRateDAOImpl;
import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.dto.ExchangeRateDTO;
import maxim.butenko.model.ExchangeRate;

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

    public List<ExchangeRateDTO> findAll() {
        return exchangeRateDAO.findAll().stream()
                .map(this::buildExchangeRate)
                .collect(toList());
    }

    public Optional<ExchangeRateDTO> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode).stream()
                .map(this::buildExchangeRate)
                .findFirst();
    }

    public Optional<ExchangeRateDTO> create(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
        var baseCurrencyDTO = currencyService.findByCode(baseCurrencyCode).orElseThrow();
        var targetCurrencyDTO = currencyService.findByCode(targetCurrencyCode).orElseThrow();

        var baseCurrency = currencyMapper.convertCurrencyDTOToCurrency(baseCurrencyDTO);
        var targetCurrency = currencyMapper.convertCurrencyDTOToCurrency(targetCurrencyDTO);

        ExchangeRate save = exchangeRateDAO.save(new ExchangeRate(null, baseCurrency, targetCurrency, rate));
        return Optional.ofNullable(buildExchangeRate(save));
    }

    public Optional<ExchangeRateDTO> update(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
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


}
