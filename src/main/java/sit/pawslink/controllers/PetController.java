package sit.pawslink.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.pawslink.dto.PetAddEditDTO;
import sit.pawslink.dto.PetDetailDTO;
import sit.pawslink.dto.PetListDTO;
import sit.pawslink.services.FileService;
import sit.pawslink.services.PetService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;
    private final FileService fileService;

    public PetController(PetService petService, FileService fileService) {
        this.petService = petService;
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<List<PetListDTO>> getAllPets() {
        List<PetListDTO> pets = petService.getAllPetsForLoggedInUser();
        return pets.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pets);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetDetailDTO> getPetById(@PathVariable Integer petId) {
        Optional<PetDetailDTO> petDetail = petService.getPetDetailForLoggedInUser(petId);
        return petDetail.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PetDetailDTO> addPet(@Valid @ModelAttribute PetAddEditDTO petAddEditDTO) {
        if (petAddEditDTO.getFile() == null || petAddEditDTO.getFile().isEmpty()) {
            throw new IllegalArgumentException("Image must not be empty");
        }
        String imageUrl = fileService.uploadImage(petAddEditDTO.getFile());
        PetDetailDTO createdPet = petService.createPetForLoggedInUser(petAddEditDTO, imageUrl);
        return ResponseEntity.ok(createdPet);
    }

    @PutMapping("/{petId}")
    public ResponseEntity<PetDetailDTO> editPet(@PathVariable Integer petId, @ModelAttribute PetAddEditDTO petAddEditDTO) {
        PetDetailDTO updatedPet = petService.updatePetForLoggedInUser(petId, petAddEditDTO);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer petId) {
        petService.deletePetByIdForLoggedInUser(petId);
        return ResponseEntity.noContent().build();
    }
}
