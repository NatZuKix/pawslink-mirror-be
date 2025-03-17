package sit.pawslink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDTO {
    private Integer contactId;

    @NotBlank(message = "Contact type must not be blank")
    private String contactType;

    @NotBlank(message = "Contact value must not be blank")
    private String contactValue;
}