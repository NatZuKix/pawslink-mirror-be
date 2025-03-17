package sit.pawslink.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.pawslink.entities.Record;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    List<Record> findByPet_PetId(Integer petId);

    @Query("SELECT r FROM Record r JOIN FETCH r.pet p WHERE p.user.userId = :userId ORDER BY r.scannedAt DESC")
    List<Record> findByUserId(Integer userId);
}
