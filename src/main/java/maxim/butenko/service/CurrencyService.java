package maxim.butenko.service;

import maxim.butenko.dao.CurrencyDAOImpl;
import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.model.Currency;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class CurrencyService {

    private final CurrencyDAOImpl currencyDAO = CurrencyDAOImpl.getInstance();

    private static final CurrencyService INSTANCE = new CurrencyService();

    private CurrencyService() {

    }

    public List<CurrencyDTO> findAll() {
        return currencyDAO.findAll().stream()
                .map(this::buildCurrency)
                .collect(toList());
    }

    public Optional<CurrencyDTO> findByCode(String code) {
        return currencyDAO.findByCode(code).stream()
                .map(this::buildCurrency)
                .findFirst();
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public Optional<CurrencyDTO> create( String fullName, String code, String sign) {
        var currency = new Currency(null, fullName, code, sign);
        currencyDAO.save(currency);
        return Optional.ofNullable(buildCurrency(currency));
    }

    private CurrencyDTO buildCurrency(Currency currency) {
        return CurrencyDTO.builder()
                .id(currency.getId())
                .fullName(currency.getFullName())
                .code(currency.getCode())
                .sign(currency.getSign())
                .build();
    }

}
