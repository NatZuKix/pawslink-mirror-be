package sit.pawslink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PetAddEditDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Species is required")
    private String species;
    @NotBlank(message = "Breed is required")
    private String breed;
    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;
    @NotBlank(message = "Gender is required")
    private String gender;
    private String bio;
    @NotBlank(message = "Status is required")
    private String status;
    private MultipartFile file;
}
