package maxim.butenko.service;

import maxim.butenko.dao.CurrencyDAOImpl;
import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class CurrencyService {

    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDAOImpl currencyDAO = CurrencyDAOImpl.getInstance();

       private CurrencyService() {

    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    private CurrencyDTO buildCurrency(Currency currency) {
        return CurrencyDTO.builder()
                .id(currency.getId())
                .fullName(currency.getFullName())
                .code(currency.getCode())
                .sign(currency.getSign())
                .build();
    }

    public List<CurrencyDTO> findAll() throws SQLException {
        return currencyDAO.findAll().stream()
                .map(this::buildCurrency)
                .collect(toList());
    }

    public Optional<CurrencyDTO> findByCode(String code) throws SQLException {
        return currencyDAO.findByCode(code).stream()
                .map(this::buildCurrency)
                .findFirst();
    }



    public Optional<CurrencyDTO> create(String fullName, String code, String sign) throws SQLException {
        Currency currency = new Currency(fullName, code, sign);
        currencyDAO.save(currency);
        return Optional.ofNullable(buildCurrency(currency));
    }



}
