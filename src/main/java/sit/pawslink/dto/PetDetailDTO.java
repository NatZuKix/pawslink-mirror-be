package sit.pawslink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDetailDTO {
    private String name;
    private String species;
    private String breed;
    private String dateOfBirth;
    private String gender;
    private String imageUrl;
    private String bio;
    private String status;
    private String profileUrl;
}