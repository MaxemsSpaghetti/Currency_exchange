package maxim.butenko.service;

import maxim.butenko.dao.ExchangeRateDAO;
import maxim.butenko.dto.ExchangeRateDTO;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ExchangeRateService implements Service<ExchangeRateDTO> {

    public static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRateDAO exchangeRateDAO = ExchangeRateDAO.getInstance();

    private ExchangeRateService() {

    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateDTO> findByCurrencyId(Long CurrencyId) {
        return exchangeRateDAO.findByCurrencyId(CurrencyId).stream()
                .map(currency -> new ExchangeRateDTO(
                        currency.getId(),
                        currency.getBaseCurrencyId(),
                        currency.getTargetCurrencyId(),
                        currency.getRate()))
                .collect(toList());
    }

    @Override
    public List<ExchangeRateDTO> findAll() {
        return null;
    }
}
