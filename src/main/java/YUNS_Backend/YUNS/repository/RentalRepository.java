package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.Rental;
import YUNS_Backend.YUNS.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByStatus(RentalStatus status);
}
