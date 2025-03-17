package sit.pawslink.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.pawslink.dto.ContactDTO;
import sit.pawslink.dto.PublicPetDTO;
import sit.pawslink.entities.Contact;
import sit.pawslink.entities.Pet;
import sit.pawslink.entities.User;
import sit.pawslink.exceptions.ResourceNotFoundException;
import sit.pawslink.repositories.ContactRepository;
import sit.pawslink.repositories.PetRepository;
import sit.pawslink.repositories.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicPetService {

    private final PetRepository petRepo;
    private final ContactRepository contactRepo;
    private final UserRepository userRepo;

    public PublicPetService(PetRepository petRepo, ContactRepository contactRepo, UserRepository userRepo) {
        this.petRepo = petRepo;
        this.contactRepo = contactRepo;
        this.userRepo = userRepo;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("User is not authenticated.");
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new IllegalStateException("User not found in database.");
        }

        return user;
    }

    public PublicPetDTO getPublicPetProfile(String profileUrl) {
        Pet petEntity = petRepo.findByProfileUrl(profileUrl)
                .orElseThrow(() -> new ResourceNotFoundException("Pet with profile URL " + profileUrl + " not found"));

        List<Contact> contactEntities = contactRepo.findAllByUser(petEntity.getUser());

        return toPublicPetDTO(petEntity, contactEntities);
    }

    public byte[] generateQrCodeForOwnedPet(String profileUrl) {
        Pet petEntity = petRepo.findByProfileUrl(profileUrl)
                .orElseThrow(() -> new ResourceNotFoundException("Pet with profile URL " + profileUrl + " not found"));

        User loggedInUser = getLoggedInUser();

        if (!loggedInUser.equals(petEntity.getUser())) {
            throw new IllegalStateException("Unauthorized access: This pet is not owned by the logged-in user");
        }

        String fullUrl = "https://capstone24.sit.kmutt.ac.th/ms1/public/" + profileUrl;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(fullUrl, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR Code", e);
        }
    }

    private PublicPetDTO toPublicPetDTO(Pet petEntity, List<Contact> contactEntities) {
        List<ContactDTO> contactDTOs = contactEntities.stream()
                .map(this::toContactDTO)
                .collect(Collectors.toList());

        if (!"NORMAL".equalsIgnoreCase(String.valueOf(petEntity.getStatus()))) {
            contactDTOs = contactEntities.stream()
                    .map(this::toContactDTO)
                    .collect(Collectors.toList());
        }

        return PublicPetDTO.builder()
                .petId(petEntity.getPetId())
                .name(petEntity.getName())
                .species(petEntity.getSpecies())
                .breed(petEntity.getBreed())
                .dateOfBirth(petEntity.getDateOfBirth())
                .gender(String.valueOf(petEntity.getGender()))
                .profileUrl(petEntity.getProfileUrl())
                .bio(petEntity.getBio())
                .imageUrl(petEntity.getImageUrl())
                .status(String.valueOf(petEntity.getStatus()))
                .contacts(contactDTOs)
                .build();
    }

    private ContactDTO toContactDTO(Contact contactEntity) {
        return ContactDTO.builder()
                .contactId(contactEntity.getContactId())
                .contactType(contactEntity.getContactType())
                .contactValue(contactEntity.getContactValue())
                .build();
    }
}
