package sit.pawslink.services;

import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.pawslink.controllers.WebSocketNotificationController;
import sit.pawslink.dto.ScanRecordResponse;
import sit.pawslink.entities.Pet;
import sit.pawslink.entities.Record;
import sit.pawslink.entities.User;
import sit.pawslink.repositories.PetRepository;
import sit.pawslink.repositories.RecordRepository;
import sit.pawslink.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RecordService {
    private final RecordRepository recordRepository;
    private final PetRepository petRepository;
    private final ReverseGeocodingService reverseGeocodingService;
    private final UserRepository userRepository;
    private final WebSocketNotificationController webSocketNotificationController;

    public RecordService(RecordRepository recordRepository, PetRepository petRepository, ReverseGeocodingService reverseGeocodingService, UserRepository userRepository, WebSocketNotificationController webSocketNotificationController) {
        this.recordRepository = recordRepository;
        this.petRepository = petRepository;
        this.reverseGeocodingService = reverseGeocodingService;
        this.userRepository = userRepository;
        this.webSocketNotificationController = webSocketNotificationController;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        throw new IllegalStateException("User is not authenticated");
    }

    @Transactional
    public Record saveRecord(Integer petId, Double latitude, Double longitude) {
        Optional<Pet> pet = petRepository.findById(petId);
        if (pet.isEmpty()) {
            throw new RuntimeException("Pet not found");
        }

        String address = reverseGeocodingService.getAddressFromCoordinates(latitude, longitude);

        Record scanRecord = new Record();
        scanRecord.setPet(pet.get());
        scanRecord.setLatitude(latitude);
        scanRecord.setLongitude(longitude);
        scanRecord.setAddress(address);

        Record savedRecord = recordRepository.save(scanRecord);
        webSocketNotificationController.sendScanNotification(new ScanRecordResponse(savedRecord));

        return savedRecord;
    }

    public List<Record> getScanRecordsByPetId(Integer petId) {
        User user = getLoggedInUser();

        Pet pet = petRepository.findByPetIdAndUser(petId, user)
                .orElseThrow(() -> new IllegalArgumentException("Unauthorized or Pet not found"));

        return recordRepository.findByPet_PetId(pet.getPetId());
    }

    public List<Record> getScanRecordsByUser() {
        User user = getLoggedInUser();
        return recordRepository.findByUserId(user.getUserId());
    }

    public void deleteScanRecord(Integer scanId) {
        User user = getLoggedInUser();

        Record scanRecord = recordRepository.findById(scanId)
                .orElseThrow(() -> new IllegalArgumentException("Scan record not found"));

        if (!scanRecord.getPet().getUser().equals(user)) {
            throw new IllegalArgumentException("Unauthorized to delete this scan record");
        }

        recordRepository.delete(scanRecord);
    }
}
