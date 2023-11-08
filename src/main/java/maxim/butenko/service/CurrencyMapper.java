package maxim.butenko.service;

import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.model.Currency;

public class CurrencyMapper {

    private static final CurrencyMapper INSTANCE = new CurrencyMapper();

    public CurrencyMapper() {

    }

    public static CurrencyMapper getInstance() {
        return INSTANCE;
    }

    public CurrencyDTO convertCurrencyToCurrencyDTO(Currency currency) {
        return CurrencyDTO.builder()
                .id(currency.getId())
                .fullName(currency.getFullName())
                .code(currency.getCode())
                .sign(currency.getSign())
                .build();
    }

    public Currency convertCurrencyDTOToCurrency(CurrencyDTO currencyDTO) {
        return Currency.builder()
                .id(currencyDTO.getId())
                .fullName(currencyDTO.getFullName())
                .code(currencyDTO.getCode())
                .sign(currencyDTO.getSign())
                .build();
    }
}
