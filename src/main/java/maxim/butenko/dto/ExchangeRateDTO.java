package maxim.butenko.dto;

import lombok.Builder;
import lombok.Value;
@Value
@Builder
public class ExchangeRateDTO {
    long id;
    int baseCurrencyId;
    int targetCurrencyId;
    double rate;
}
