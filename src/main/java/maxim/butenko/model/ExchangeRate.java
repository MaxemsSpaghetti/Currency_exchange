package maxim.butenko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRate {
    private long id;
    private Currency baseCurrencyId;
    private Currency targetCurrencyId;
    private double rate;
}
