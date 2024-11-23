package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 특정 유저의 질문들을 조회하는 메서드
    List<Question> findByUser_StudentNumber(String studentNumber);
}
