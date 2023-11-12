package maxim.butenko.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurrencyDTO {
    Long id;
    String fullName;
    String code;
    @JsonRawValue
    String sign;
}
