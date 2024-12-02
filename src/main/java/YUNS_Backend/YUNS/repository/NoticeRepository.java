package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findByUser_StudentNumber(String studentNumber);
}
