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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final S3Service s3Service;

    // 1:1 문의 리스트 조회
    @GetMapping("/api/questions/read")
    public ResponseEntity<Map<String, Object>> getPaginatedQuestions(@RequestParam(defaultValue = "1") int page) {
        int pageSize = 10;  // 한 페이지에 표시할 항목 수
        List<QuestionDto> paginatedQuestions = questionService.getQuestionsByPage(page, pageSize);

        // 응답을 요구사항에 맞는 형식으로 변환
        List<Map<String, Object>> questions = paginatedQuestions.stream()
                .map(question -> {
                    Map<String, Object> questionMap = new HashMap<>();
                    questionMap.put("questionId", question.getQuestionId());
                    questionMap.put("title", question.getTitle());
                    questionMap.put("writer", question.getUserStudentNumber());
                    questionMap.put("date", question.getDate().toLocalDate().toString());
                    questionMap.put("state", question.isState());
                    return questionMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("questions", questions);
        return ResponseEntity.ok(response);
    }

    // 1:1 문의 세부 조회
    @GetMapping("/api/questions/{id}/read")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        Optional<QuestionDto> question = questionService.getQuestionById(id);
        return question.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 1:1 문의 작성
    @PostMapping("/api/questions/create")
    public ResponseEntity<QuestionDto> createQuestion(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @RequestParam("title") String title,
                                                      @RequestParam("content") String content,
                                                      @RequestParam(value = "image", required = false) MultipartFile image) {

        String studentNumber = userDetails.getUsername();
        User user = userService.findUserByStudentNumber(studentNumber);

        // 이미지 업로드 처리
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);  // S3에 이미지 업로드
        }

        // QuestionDto 생성
        QuestionDto dto = QuestionDto.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .date(LocalDateTime.now())
                .state(false)
                .build();

        QuestionDto createdQuestion = questionService.createQuestion(dto, user);
        return ResponseEntity.ok(createdQuestion);
    }

    // 1:1 문의 수정
    @PutMapping("/api/questions/{id}/update")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Long id,
                                                      @RequestParam(value = "title", required = false) String title,
                                                      @RequestParam(value = "content", required = false) String content,
                                                      @RequestParam(value = "image", required = false) MultipartFile image,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        Optional<QuestionDto> question = questionService.getQuestionById(id);

        // 작성자 검증 - 로그인한 사용자가 관리자이거나 글 작성자와 동일한 학번인지 확인
        if (question.isPresent()) {
            String loggedInStudentNumber = userDetails.getUsername();
            String questionOwnerStudentNumber = question.get().getUserStudentNumber();

            // 작성자가 아니고 관리자가 아니라면 403 에러 반환
            if (!loggedInStudentNumber.equals(questionOwnerStudentNumber) &&
                    !userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // 이미지 업로드 처리
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);  // S3에 이미지 업로드
        }

        // QuestionDto 업데이트
        QuestionDto updatedDto = QuestionDto.builder()
                .title(title != null ? title : question.get().getTitle())
                .content(content != null ? content : question.get().getContent())
                .imageUrl(imageUrl != null ? imageUrl : question.get().getImageUrl())
                .date(question.get().getDate())
                .state(question.get().isState())
                .answer(question.get().getAnswer())
                .userStudentNumber(question.get().getUserStudentNumber())  // userStudentNumber로 저장
                .build();

        Optional<QuestionDto> updatedQuestion = questionService.updateQuestion(id, updatedDto);
        return updatedQuestion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 1:1 문의 삭제
    @DeleteMapping("/api/questions/{id}/delete")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        Optional<QuestionDto> question = questionService.getQuestionById(id);

        // 문의가 없으면 404 응답 반환
        if (question.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("문의가 존재하지 않습니다.");  // 404 응답
        }

        // 작성자 검증 - 로그인한 사용자가 관리자이거나 글 작성자와 동일한 학번인지 확인
        if (question.isPresent()) {
            String loggedInStudentNumber = userDetails.getUsername();  // 로그인한 사용자의 학번
            String questionOwnerStudentNumber = question.get().getUserStudentNumber();  // 글 작성자의 학번

            // 작성자가 아니고 관리자가 아니라면 403 에러 반환
            if (!loggedInStudentNumber.equals(questionOwnerStudentNumber) &&
                    !userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  // 권한 거부
            }
        }

        // 작성자가 맞거나 관리자일 경우 삭제 진행
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("문의가 성공적으로 삭제되었습니다.");
    }

    // 1:1 문의 조회
    @GetMapping("/api/my/questions")
    public ResponseEntity<List<QuestionDto>> getMyQuestions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String studentNumber = userDetails.getUsername();
        List<QuestionDto> myQuestions = questionService.getQuestionsByStudentNumber(studentNumber);

        return ResponseEntity.ok(myQuestions);
    }

    // 1:1 문의 답변 작성
    @PostMapping("/api/admin/userquestions/create")
    public ResponseEntity<Map<String, String>> createAnswer(@RequestBody Map<String, Object> request) {
        Long questionId = Long.valueOf(request.get("questionId").toString());
        String answer = request.get("answer").toString();

        Optional<QuestionDto> updatedQuestion = questionService.answerQuestion(questionId, answer);
        if (updatedQuestion.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("answer", updatedQuestion.get().getAnswer());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 해당 질문이 없으면 404 응답
        }
    }


}
