package sit.pawslink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetListDTO {
    private Integer petId;
    private String name;
    private String imageUrl;
    private String breed;
    private String status;
    private String profileUrl;
}
