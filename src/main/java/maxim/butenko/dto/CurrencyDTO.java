package maxim.butenko.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurrencyDTO {
    Long id;
    String fullName;
    String code;
    String sign;
}
