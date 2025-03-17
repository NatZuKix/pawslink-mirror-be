package sit.pawslink.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private User existingUser;
    private String userId;

    public CustomUserDetails(User existingUser, String userId) {

        this.existingUser = existingUser;
        this.userId = userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Populate with user's authorities/roles
        // You can convert your User's roles to GrantedAuthority objects here
        // For example, you can map roles to SimpleGrantedAuthority instances.
        // Return a collection of these authorities.
        // Example: return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        String role = existingUser.getRole().toUpperCase();
//        System.out.println("Role: "+role);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        return Collections.singleton(authority);
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return existingUser.getEmail();
    }
    @Override
    public String getPassword() {
        return existingUser.getPassword();
    }

    @Override
    public String getUsername() {
        return existingUser.getUsername();
    }

    // Implement other UserDetails methods like isAccountNonExpired, isAccountNonLocked, etc.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

