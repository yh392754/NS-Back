package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByStudentNumber(String studentNumber);
    Optional<User> findByUserId(Long userId);
}
