package maxim.butenko.dto;

import lombok.Builder;
import lombok.Value;
@Value
@Builder
public class ExchangeRateDTO {
    long id;
    CurrencyDTO baseCurrencyId;
    CurrencyDTO targetCurrencyId;
    double rate;
}
