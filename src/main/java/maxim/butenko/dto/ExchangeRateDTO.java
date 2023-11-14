package maxim.butenko.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeRateDTO {
    long id;
    CurrencyDTO baseCurrencyId;
    CurrencyDTO targetCurrencyId;
    BigDecimal rate;
}
