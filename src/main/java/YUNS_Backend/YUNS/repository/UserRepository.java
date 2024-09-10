package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByStudentNumber(String studentNumber);
}
