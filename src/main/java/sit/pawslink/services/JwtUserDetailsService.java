package sit.pawslink.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sit.pawslink.entities.CustomUserDetails;
import sit.pawslink.entities.User;
import sit.pawslink.repositories.UserRepository;

import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User existingUser = userRepository.findByUsername(username);
        String userId = String.valueOf(existingUser.getUserId());
        if(existingUser == null){
            throw new UsernameNotFoundException("User not found: "+ username);
        }
        UserDetails userDetails = new CustomUserDetails(existingUser, userId);
        return userDetails;
    }
}