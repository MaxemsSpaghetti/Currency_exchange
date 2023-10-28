package maxim.butenko.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurrencyDTO {
    Long id;
    String code;
    String fullName;
    String sign;
}
