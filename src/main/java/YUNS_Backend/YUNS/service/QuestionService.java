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

    // 특정 질문을 DTO로 반환
    public Optional<QuestionDto> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId).map(this::convertToDto);
    }

    // 질문 생성 및 DTO 반환
    public QuestionDto createQuestion(QuestionDto dto, User user) {
        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(dto.getImageUrl())
                .state(false)
                .date(LocalDateTime.now())
                .user(user)  // 작성자 정보 추가
                .build();

        Question savedQuestion = questionRepository.save(question);
        return convertToDto(savedQuestion);
    }

    // 질문 수정 및 DTO 반환 (빌더 패턴 사용)
    public Optional<QuestionDto> updateQuestion(Long questionId, QuestionDto dto) {
        return questionRepository.findById(questionId).map(existingQuestion -> {
            // 기존 질문을 빌더 패턴으로 수정
            Question updatedQuestion = Question.builder()
                    .questionId(existingQuestion.getQuestionId())  // 기존 ID 유지
                    .title(dto.getTitle() != null ? dto.getTitle() : existingQuestion.getTitle())  // 수정된 제목 또는 기존 제목
                    .content(dto.getContent() != null ? dto.getContent() : existingQuestion.getContent())  // 수정된 내용 또는 기존 내용
                    .imageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : existingQuestion.getImageUrl())  // 수정된 이미지 또는 기존 이미지
                    .date(existingQuestion.getDate())  // 기존 날짜 유지
                    .state(existingQuestion.isState())  // 기존 상태 유지
                    .answer(existingQuestion.getAnswer())  // 기존 답변 유지
                    .user(existingQuestion.getUser())  // 기존 작성자 유지
                    .build();

            Question savedQuestion = questionRepository.save(updatedQuestion);
            return convertToDto(savedQuestion);
        });
    }

    // 질문 삭제
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
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

    public Optional<QuestionDto> answerQuestion(Long questionId, String answer) {
        return questionRepository.findById(questionId).map(existingQuestion -> {
            // 빌더 패턴을 사용하여 기존 질문 객체를 기반으로 새로운 객체 생성
            Question updatedQuestion = Question.builder()
                    .questionId(existingQuestion.getQuestionId())
                    .title(existingQuestion.getTitle())
                    .content(existingQuestion.getContent())
                    .date(existingQuestion.getDate())
                    .state(true) // 답변 상태를 완료로 변경
                    .answer(answer) // 답변 추가
                    .imageUrl(existingQuestion.getImageUrl())
                    .user(existingQuestion.getUser())
                    .build();

            Question savedQuestion = questionRepository.save(updatedQuestion);
            return convertToDto(savedQuestion);
        });
    }


    public List<QuestionDto> getQuestionsByStudentNumber(String studentNumber) {
        return questionRepository.findByUser_StudentNumber(studentNumber)
                .stream()
                .map(Question.QuestionMapper::toDto)
                .collect(Collectors.toList());
    }

}
