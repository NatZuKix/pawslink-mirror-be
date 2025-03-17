package sit.pawslink.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sit.pawslink.dto.PublicPetDTO;
import sit.pawslink.exceptions.ResourceNotFoundException;
import sit.pawslink.services.PublicPetService;

import java.util.Collection;

@RestController
@RequestMapping("/api/public")
public class PublicPetController {

    private final PublicPetService publicPetService;

    public PublicPetController(PublicPetService publicPetService) {
        this.publicPetService = publicPetService;
    }

    @GetMapping("/{profileUrl}")
    public ResponseEntity<PublicPetDTO> getPublicPetProfile(@PathVariable("profileUrl") String profileUrl) {
        PublicPetDTO publicPetProfile = publicPetService.getPublicPetProfile(profileUrl);
        return ResponseEntity.ok(publicPetProfile);
    }

    @GetMapping("/generate-qr")
    public ResponseEntity<byte[]> generateQrCode(@RequestParam String profileUrl) {
        try {
            byte[] qrCode = publicPetService.generateQrCodeForOwnedPet(profileUrl);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr-code.png")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Unauthorized access
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Profile URL not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Other errors
        }
    }

    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ User is not authenticated");
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return ResponseEntity.ok("✅ Authenticated as: " + username + " | Roles: " + authorities);
    }
}
