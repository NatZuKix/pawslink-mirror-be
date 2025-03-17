package sit.pawslink.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.pawslink.dto.ContactDTO;
import sit.pawslink.exceptions.ResourceNotFoundException;
import sit.pawslink.services.ContactService;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getContacts() {
//        List<ContactDTO> contacts = contactService.getContacts();
        List<ContactDTO> contacts = contactService.getAllContactsForLoggedInUser();
        return contacts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(contacts);
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Integer contactId) {
//        ContactDTO contact = contactService.getContactById(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + contactId));
        ContactDTO contact = contactService.getContactByIdForLoggedInUser(contactId).orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + contactId));
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    public ResponseEntity<ContactDTO> createContact(@Valid @RequestBody ContactDTO contactDTO) {
//        ContactDTO newContact = contactService.createContact(contactDTO);
        ContactDTO newContact = contactService.createContactForLoggedInUser(contactDTO);
        return ResponseEntity.ok(newContact);
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable Integer contactId,
            @RequestBody ContactDTO contactDTO) {
//        ContactDTO updatedContact = contactService.updateContact(contactId, contactDTO);
        ContactDTO updatedContact = contactService.updateContactForLoggedInUser(contactId, contactDTO);
        return ResponseEntity.ok(updatedContact);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<ContactDTO> deleteContact(@PathVariable Integer contactId) {
//        contactService.deleteContact(contactId);
        contactService.deleteContactForLoggedInUser(contactId);
        return ResponseEntity.noContent().build();
    }
}
