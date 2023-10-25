package maxim.butenko.service;

import maxim.butenko.dao.CurrencyDAOImpl;
import maxim.butenko.dto.CurrencyDTO;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyDAOImpl currencyDAO = CurrencyDAOImpl.getInstance();

    public static final CurrencyServiceImpl INSTANCE = new CurrencyServiceImpl();

    private CurrencyServiceImpl() {

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

    public static CurrencyServiceImpl getInstance() {
        return INSTANCE;
    }
}
