package maxim.butenko.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Currency {
    private Long id;
    private String fullName;
    private String code;
    @JsonRawValue
    private String sign;
}
