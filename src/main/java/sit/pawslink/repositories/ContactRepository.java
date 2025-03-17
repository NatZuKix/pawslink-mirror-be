package sit.pawslink.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.pawslink.entities.Contact;
import sit.pawslink.entities.User;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    List<Contact> findByUser_UserId(Integer userId);
    List<Contact> findAllByUser(User user);
    Optional<Contact> findByContactIdAndUser(Integer contactId, User user);
}
