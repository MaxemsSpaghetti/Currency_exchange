package maxim.butenko.service;

import maxim.butenko.dto.CurrencyDTO;
import maxim.butenko.model.Currency;
import org.modelmapper.ModelMapper;

public class CurrencyMapper extends ModelMapper {

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
}