package sit.pawslink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicPetDTO {
    private Integer petId;
    private String name;
    private String species;
    private String breed;
    private LocalDate dateOfBirth;
    private String gender;
    private String profileUrl;
    private String bio;
    private String imageUrl;
    private String status;

    private List<ContactDTO> contacts;
}
