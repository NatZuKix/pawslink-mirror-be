package sit.pawslink.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.pawslink.dto.PetAddEditDTO;
import sit.pawslink.dto.PetDetailDTO;
import sit.pawslink.dto.PetListDTO;
import sit.pawslink.entities.Pet;
import sit.pawslink.entities.User;
import sit.pawslink.exceptions.PetNotFoundException;
import sit.pawslink.exceptions.ResourceNotFoundException;
import sit.pawslink.repositories.PetRepository;
import sit.pawslink.repositories.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetService {

    private final PetRepository petRepo;
    private final UserRepository userRepo;
    private final FileService fileService;

    public PetService(PetRepository petRepo, UserRepository userRepo, FileService fileService) {
        this.petRepo = petRepo;
        this.userRepo = userRepo;
        this.fileService = fileService;
    }


    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername(); // Get username instead of email
            return userRepo.findByUsername(username); // Use findByUsername instead of findByEmail
        }
        throw new IllegalStateException("User is not authenticated");
    }

    public List<PetListDTO> getAllPetsForLoggedInUser() {
        User user = getLoggedInUser();
        List<Pet> pets = petRepo.findAllByUser(user);
        return pets.stream().map(this::toPetListDTO).collect(Collectors.toList());
    }

    public Optional<PetDetailDTO> getPetDetailForLoggedInUser(Integer petId) {
        User user = getLoggedInUser();
        return petRepo.findByPetIdAndUser(petId, user).map(this::toPetDetailDTO);
    }

    public PetDetailDTO createPetForLoggedInUser(PetAddEditDTO petAddEditDTO, String imageUrl) {
        User user = getLoggedInUser();

        String baseProfileUrl = generateUniqueProfileUrl();

        Pet newPet = toPetEntity(petAddEditDTO, imageUrl);
        newPet.setUser(user);
        newPet.setProfileUrl(baseProfileUrl);

        Pet savedPet = petRepo.save(newPet);
        return toPetDetailDTO(savedPet);
    }

    private String generateUniqueProfileUrl() {
        String profileUrl;
        do {
            profileUrl = generateShortProfileUrl();
        } while (petRepo.findByProfileUrl(profileUrl).isPresent()); // ถ้าซ้ำให้สร้างใหม่

        return profileUrl;
    }

    private String generateShortProfileUrl() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) { // 8 ตัวอักษร
            sb.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    public PetDetailDTO updatePetForLoggedInUser(Integer petId, PetAddEditDTO petAddEditDTO) {
        User user = getLoggedInUser();
        Pet petEntity = petRepo.findByPetIdAndUser(petId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found or unauthorized."));

        // Update fields
        if (petAddEditDTO.getName() != null) petEntity.setName(petAddEditDTO.getName());
        if (petAddEditDTO.getSpecies() != null) petEntity.setSpecies(petAddEditDTO.getSpecies());
        if (petAddEditDTO.getBreed() != null) petEntity.setBreed(petAddEditDTO.getBreed());
        if (petAddEditDTO.getDateOfBirth() != null)
            petEntity.setDateOfBirth(LocalDate.parse(petAddEditDTO.getDateOfBirth()));
        if (petAddEditDTO.getGender() != null)
            petEntity.setGender(Pet.Gender.valueOf(petAddEditDTO.getGender().toUpperCase()));
        if (petAddEditDTO.getBio() != null) petEntity.setBio(petAddEditDTO.getBio());
        if (petAddEditDTO.getStatus() != null)
            petEntity.setStatus(Pet.Status.valueOf(petAddEditDTO.getStatus().toUpperCase()));

        if (petAddEditDTO.getFile() != null) {
            if (petEntity.getImageUrl() != null) {
                fileService.deleteImage(petEntity.getImageUrl());
            }
            String newImageUrl = fileService.uploadImage(petAddEditDTO.getFile());
            petEntity.setImageUrl(newImageUrl);
        }

        Pet updatedPet = petRepo.save(petEntity);
        return toPetDetailDTO(updatedPet);
    }

    public void deletePetByIdForLoggedInUser(Integer petId) {
        User user = getLoggedInUser();
        Pet pet = petRepo.findByPetIdAndUser(petId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found or unauthorized."));

        if (pet.getImageUrl() != null) {
            fileService.deleteImage(pet.getImageUrl());
        }
        petRepo.delete(pet);
    }

    private PetListDTO toPetListDTO(Pet petEntity) {
        return PetListDTO.builder()
                .petId(petEntity.getPetId())
                .name(petEntity.getName())
                .imageUrl(petEntity.getImageUrl())
                .breed(petEntity.getBreed())
                .status(String.valueOf(petEntity.getStatus()))
                .profileUrl(petEntity.getProfileUrl())
                .build();
    }

    private PetDetailDTO toPetDetailDTO(Pet petEntity) {
        return PetDetailDTO.builder()
                .name(petEntity.getName())
                .species(petEntity.getSpecies())
                .breed(petEntity.getBreed())
                .dateOfBirth(String.valueOf(petEntity.getDateOfBirth()))
                .gender(String.valueOf(petEntity.getGender()))
                .imageUrl(petEntity.getImageUrl())
                .bio(petEntity.getBio())
                .status(String.valueOf(petEntity.getStatus()))
                .profileUrl(petEntity.getProfileUrl())
                .build();
    }

    private Pet toPetEntity(PetAddEditDTO dto, String imageUrl) {
        return Pet.builder()
                .name(dto.getName())
                .species(dto.getSpecies())
                .breed(dto.getBreed())
                .dateOfBirth(Optional.ofNullable(dto.getDateOfBirth())
                        .map(LocalDate::parse).orElse(null))
                .gender(Pet.Gender.valueOf(dto.getGender().toUpperCase()))
                .bio(dto.getBio())
                .imageUrl(imageUrl)
                .status(Pet.Status.valueOf(dto.getStatus().toUpperCase()))
                .build();
    }
}