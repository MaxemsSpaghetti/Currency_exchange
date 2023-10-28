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
    long id;
    int baseCurrencyId;
    int targetCurrencyId;
    double rate;
}
