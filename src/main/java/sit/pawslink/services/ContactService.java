package sit.pawslink.services;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.pawslink.dto.ContactDTO;
import sit.pawslink.entities.Contact;
import sit.pawslink.entities.User;
import sit.pawslink.exceptions.ResourceNotFoundException;
import sit.pawslink.repositories.ContactRepository;
import sit.pawslink.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {
    private final ContactRepository contactRepo;
    private final UserRepository userRepo;

    public ContactService(ContactRepository contactRepo, UserRepository userRepo) {
        this.contactRepo = contactRepo;
        this.userRepo = userRepo;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepo.findByUsername(username);
        }
        throw new IllegalStateException("User is not authenticated");
    }

    public List<ContactDTO> getAllContactsForLoggedInUser() {
        User user = getLoggedInUser();
        List<Contact> contacts = contactRepo.findAllByUser(user);
        return contacts.stream().map(this::toContactDTO).collect(Collectors.toList());
    }

    public Optional<ContactDTO> getContactByIdForLoggedInUser(Integer contactId) {
        User user = getLoggedInUser();
        return contactRepo.findByContactIdAndUser(contactId, user).map(this::toContactDTO);
    }

    public ContactDTO createContactForLoggedInUser(@Valid ContactDTO contactDTO) {
        User user = getLoggedInUser();

        Contact contactEntity = toContactEntity(contactDTO, user);
        Contact savedContactEntity = contactRepo.save(contactEntity);
        return toContactDTO(savedContactEntity);
    }

    public ContactDTO updateContactForLoggedInUser(Integer id, @Valid ContactDTO contactDTO) {
        User loggedInUser = getLoggedInUser();

        Contact contactEntity = contactRepo.findByContactIdAndUser(id, loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found or you don't have permission"));

        // Update fields
        if (contactDTO.getContactType() != null) {
            contactEntity.setContactType(contactDTO.getContactType());
        }
        if (contactDTO.getContactValue() != null) {
            contactEntity.setContactValue(contactDTO.getContactValue());
        }

        Contact updatedContactEntity = contactRepo.save(contactEntity);
        return toContactDTO(updatedContactEntity);
    }

    public void deleteContactForLoggedInUser(Integer id) {
        User loggedInUser = getLoggedInUser();

        Contact contactEntity = contactRepo.findByContactIdAndUser(id, loggedInUser)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found or you don't have permission"));

        contactRepo.delete(contactEntity);
    }

    public List<ContactDTO> getContacts() {
        List<Contact> contacts = contactRepo.findAll();
        return contacts.stream().map(this::toContactDTO).collect(Collectors.toList());
    }

    public Optional<ContactDTO> getContactById(Integer id) {
        return contactRepo.findById(id).map(this::toContactDTO);
    }

    public ContactDTO updateContact(Integer id, @Valid ContactDTO contactDTO) {
        Contact contactEntity = contactRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        if (contactDTO.getContactType() != null) {
            contactEntity.setContactType(contactDTO.getContactType());
        }
        if (contactDTO.getContactValue() != null) {
            contactEntity.setContactValue(contactDTO.getContactValue());
        }

        Contact updatedContactEntity = contactRepo.save(contactEntity);
        return toContactDTO(updatedContactEntity);
    }

    public void deleteContact(Integer id) {
        if (!contactRepo.existsById(id)) {
            throw new ResourceNotFoundException("Contact not found");
        }
        contactRepo.deleteById(id);
    }

    private ContactDTO toContactDTO(Contact contactEntity) {
        return ContactDTO.builder()
                .contactId(contactEntity.getContactId())
                .contactType(contactEntity.getContactType())
                .contactValue(contactEntity.getContactValue())
                .build();
    }

    private Contact toContactEntity(ContactDTO contactDTO, User user) {
        return Contact.builder()
                .user(user)
                .contactType(contactDTO.getContactType())
                .contactValue(contactDTO.getContactValue())
                .build();
    }
}

