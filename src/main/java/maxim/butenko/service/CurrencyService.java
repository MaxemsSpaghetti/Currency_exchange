package maxim.butenko.service;

import maxim.butenko.dao.CurrencyDAO;
import maxim.butenko.dto.CurrencyDTO;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CurrencyService implements Service<CurrencyDTO> {

    private final CurrencyDAO currencyDAO = CurrencyDAO.getInstance();

    public static final CurrencyService INSTANCE = new CurrencyService();

    private CurrencyService() {

    }

    @Override
    public List<CurrencyDTO> findAll() {
        return currencyDAO.findAll().stream()
                .map(currency -> new CurrencyDTO(
                        currency.getId(),
                        currency.getCode(),
                        currency.getFullName(),
                        currency.getSign()))
                .collect(toList());
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
