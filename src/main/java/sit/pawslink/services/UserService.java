package sit.pawslink.services;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import sit.pawslink.entities.User;
import sit.pawslink.exceptions.UserAlreadyExistsException;
import sit.pawslink.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Argon2PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 4096, 1);
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email '" + email + "' is already registered.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password

        return userRepository.save(user);
    }
}
