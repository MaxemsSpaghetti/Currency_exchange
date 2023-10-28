package maxim.butenko.service;

import maxim.butenko.dao.CurrencyDAOImpl;
import maxim.butenko.dto.CurrencyDTO;

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
                .map(currency -> CurrencyDTO.builder()
                        .id(currency.getId())
                        .fullName(currency.getFullName())
                        .code(currency.getCode())
                        .sign(currency.getSign())
                        .build())
                .collect(toList());
    }

    public Optional<CurrencyDTO> findByCode(String code) {
        return currencyDAO.findByCode(code).stream()
                .map(currency -> CurrencyDTO.builder()
                        .id(currency.getId())
                        .fullName(currency.getFullName())
                        .code(currency.getCode())
                        .sign(currency.getSign())
                        .build())
                .findFirst();
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
