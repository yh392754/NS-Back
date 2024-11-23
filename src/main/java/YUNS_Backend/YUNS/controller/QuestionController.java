package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.QuestionDto;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.service.QuestionService;
import YUNS_Backend.YUNS.service.S3Service;
import YUNS_Backend.YUNS.service.UserService;
import YUNS_Backend.YUNS.custom.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final S3Service s3Service; // S3 업로드를 처리할 서비스

    // 1. 1:1 문의 리스트 조회
    @GetMapping("/api/questions/read")
    public ResponseEntity<?> getAllQuestions() {
        // 서비스에서 모든 질문을 가져옴
        List<QuestionDto> questions = questionService.getAllQuestions();

        // 명세에 맞게 변환
        List<Object> responseQuestions = questions.stream()
                .map(question -> new Object() {
                    public final Long questionId = question.getQuestionId();
                    public final String title = question.getTitle();
                    public final String writer = question.getUserStudentNumber();
                    public final String date = question.getDate().toLocalDate().toString();
                    public final boolean state = question.isState();
                })
                .collect(Collectors.toList());

        // 최종 응답
        return ResponseEntity.ok(new Object() {
            public final List<Object> questions = responseQuestions;
        });
    }


    // 2. 1:1 문의 세부 조회
    @GetMapping("/api/questions/{id}/read")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        Optional<QuestionDto> questionOptional = questionService.getQuestionById(id);

        if (questionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        QuestionDto question = questionOptional.get();
        Object responseQuestion = new Object() {
            public final Long questionId = question.getQuestionId();
            public final String title = question.getTitle();
            public final String content = question.getContent();
            public final String date = question.getDate().toString();
            public final boolean state = question.isState();
            public final String answer = question.getAnswer();
            public final String imageUrl = question.getImageUrl();
            public final String writer = question.getUserStudentNumber();
        };

        return ResponseEntity.ok(new Object() {
            public final List<Object> questions = List.of(responseQuestion);
        });
    }

    // 3. 1:1 문의 작성
    @PostMapping("/api/questions/create")
    public ResponseEntity<?> createQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        String studentNumber = userDetails.getUsername();
        User user = userService.findUserByStudentNumber(studentNumber);

        // 이미지 업로드 처리
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image); // S3에 이미지 업로드
        }

        QuestionDto dto = QuestionDto.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .date(LocalDateTime.now())
                .state(false)
                .build();

        questionService.createQuestion(dto, user);

        return ResponseEntity.ok(new Object() {
            public final String message = "문의가 성공적으로 등록이 완료되었습니다.";
        });
    }
    // 4. 1:1 문의 수정
    @PutMapping("/api/questions/{id}/update")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Optional<QuestionDto> question = questionService.getQuestionById(id);

        if (question.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Object() {
                public final String message = "문의가 존재하지 않습니다.";
            });
        }

        String loggedInStudentNumber = userDetails.getUsername();
        String questionOwnerStudentNumber = question.get().getUserStudentNumber();

        if (!loggedInStudentNumber.equals(questionOwnerStudentNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Object() {
                public final String message = "권한이 없습니다.";
            });
        }

        // 이미지 업로드 처리
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);
        }

        // QuestionDto 업데이트
        QuestionDto updatedDto = QuestionDto.builder()
                .title(title != null ? title : question.get().getTitle())
                .content(content != null ? content : question.get().getContent())
                .imageUrl(imageUrl != null ? imageUrl : question.get().getImageUrl())
                .date(question.get().getDate())
                .state(question.get().isState())
                .answer(question.get().getAnswer())
                .userStudentNumber(question.get().getUserStudentNumber())
                .build();

        Optional<QuestionDto> updatedQuestion = questionService.updateQuestion(id, updatedDto);

        if (updatedQuestion.isPresent()) {
            return ResponseEntity.ok(new Object() {
                public final String message = "문의가 성공적으로 수정이 완료되었습니다.";
            });
        } else {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Object() {
                public final String message = "문의 수정 중 오류가 발생했습니다.";
            });
        }
    }

    // 5. 1:1 문의 삭제
    @DeleteMapping("/api/questions/{id}/delete")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Optional<QuestionDto> question = questionService.getQuestionById(id);

        if (question.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Object() {
                public final String message = "문의가 존재하지 않습니다.";
            });
        }

        String loggedInStudentNumber = userDetails.getUsername();
        String questionOwnerStudentNumber = question.get().getUserStudentNumber();

        if (!loggedInStudentNumber.equals(questionOwnerStudentNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Object() {
                public final String message = "권한이 없습니다.";
            });
        }

        questionService.deleteQuestion(id);

        return ResponseEntity.ok(new Object() {
            public final String message = "문의가 성공적으로 삭제되었습니다.";
        });
    }
}
