package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE r.user.studentNumber = :studentNumber")
    Optional<Rental> findByStudentNumber(@Param("studentNumber") String studentNumber);

    List<Rental> findByUser_UserId(Long userId);
}
