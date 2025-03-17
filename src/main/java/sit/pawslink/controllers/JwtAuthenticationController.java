package sit.pawslink.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sit.pawslink.config.JwtTokenUtil;
import sit.pawslink.dto.JwtRefreshResponse;
import sit.pawslink.dto.JwtRequest;
import sit.pawslink.dto.JwtResponse;
import sit.pawslink.entities.User;
import sit.pawslink.exceptions.JwtErrorResponse;
import sit.pawslink.repositories.UserRepository;
import sit.pawslink.services.JwtUserDetailsService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/login")
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        if (authenticationRequest.getUsername().isEmpty() || authenticationRequest.getPassword().isEmpty()) {
            String message = "Have something wrong in username and password, it's can be null or blank";
            JwtErrorResponse response = new JwtErrorResponse(HttpStatus.UNAUTHORIZED.value(), message);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        User existingUser = userRepository.findByUsername(authenticationRequest.getUsername());
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            Map<String, String> tokenMap = jwtTokenUtil.generateTokens(userDetails);

            JwtResponse jwtResponse = new JwtResponse(
                    tokenMap.get("access_token"),
                    tokenMap.get("refresh_token")
            );

            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e){
            if(existingUser == null){
                String message = "The specified username DOES NOT exist";
                JwtErrorResponse response = new JwtErrorResponse(HttpStatus.NOT_FOUND.value(), message);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16,32,1, 4096,1);
            boolean isMatch = argon2PasswordEncoder.matches(authenticationRequest.getPassword(), existingUser.getPassword());
            if(!isMatch){
                String message = "Password is incorrect";
                JwtErrorResponse response = new JwtErrorResponse(HttpStatus.UNAUTHORIZED.value(), message);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


    }

    @GetMapping
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;
        jwtToken = requestTokenHeader.substring(7);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(jwtToken));
        Map<String, String> tokenMap = jwtTokenUtil.generateTokens(userDetails);
        JwtRefreshResponse jwtResponse = new JwtRefreshResponse(tokenMap.get("access_token"));
        return ResponseEntity.ok(jwtResponse);
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("userId", user.getUserId());
        userProfile.put("username", user.getUsername());
        userProfile.put("email", user.getEmail());

        return ResponseEntity.ok(userProfile);
    }
}

