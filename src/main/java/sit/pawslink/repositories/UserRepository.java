package sit.pawslink.repositories;

import sit.pawslink.entities.User;
import java.util.Optional;


public interface UserRepository extends CustomRepository<User, Integer> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
