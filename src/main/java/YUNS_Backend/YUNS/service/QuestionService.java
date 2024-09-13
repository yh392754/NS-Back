package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.QuestionDto;
import YUNS_Backend.YUNS.entity.Question;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;

    // 모든 질문 리스트를 DTO로 반환
    public List<QuestionDto> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        return questions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private QuestionDto convertToDto(Question question) {
        return QuestionDto.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .content(question.getContent())
                .date(question.getDate())
                .state(question.isState())
                .answer(question.getAnswer())
                .imageUrl(question.getImageUrl())
                .userStudentNumber(question.getUser().getStudentNumber())  // 작성자의 학번 추가
                .build();
    }
}
