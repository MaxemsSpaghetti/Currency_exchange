package maxim.butenko.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonPropertyOrder({"id", "name", "code", "sign"})
public class CurrencyDTO {
    Long id;
    @JsonProperty("name")
    String fullName;
    String code;
    String sign;

}
