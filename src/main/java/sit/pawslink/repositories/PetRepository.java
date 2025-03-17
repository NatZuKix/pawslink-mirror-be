package sit.pawslink.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.pawslink.entities.Pet;
import sit.pawslink.entities.User;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    Optional<Pet> findByProfileUrl(String profileUrl);
    boolean existsByProfileUrl(String profileUrl);
    List<Pet> findAllByUser(User user);
    Optional<Pet> findByPetIdAndUser(Integer petId, User user);
}
