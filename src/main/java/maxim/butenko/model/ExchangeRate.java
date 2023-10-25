package maxim.butenko.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRate {

    private long id;

    private String baseCurrencyId;

    private String targetCurrencyId;

    private double rate;
}
