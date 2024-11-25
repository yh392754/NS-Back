package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.QuestionDto;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.service.QuestionService;
import YUNS_Backend.YUNS.service.S3Service;
import YUNS_Backend.YUNS.service.UserService;
import YUNS_Backend.YUNS.auth.CustomUserDetails;
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
                    questionMap.put("imageUrl", question.getImageUrl());
                    questionMap.put("imageUrl2", question.getImageUrl2());
                    questionMap.put("imageUrl3", question.getImageUrl3());
                    return questionMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("questions", questions);
        return ResponseEntity.ok(response);
    }

    // 1:1 문의 세부 조회
    @GetMapping("/api/questions/{id}/read")
    public ResponseEntity<Map<String, Object>> getQuestionById(@PathVariable Long id) {
        Optional<QuestionDto> question = questionService.getQuestionById(id);

        if (question.isPresent()) {
            // 응답 데이터 생성
            Map<String, Object> questionDetails = new HashMap<>();
            questionDetails.put("questionId", question.get().getQuestionId());
            questionDetails.put("title", question.get().getTitle());
            questionDetails.put("content", question.get().getContent());
            questionDetails.put("date", question.get().getDate().toString());
            questionDetails.put("state", question.get().isState());
            questionDetails.put("answer", question.get().getAnswer());
            questionDetails.put("imageUrl", question.get().getImageUrl());
            questionDetails.put("imageUrl2", question.get().getImageUrl2());
            questionDetails.put("imageUrl3", question.get().getImageUrl3());
            questionDetails.put("writer", question.get().getUserStudentNumber());

            // 응답 JSON 구조 생성
            Map<String, Object> response = new HashMap<>();
            response.put("questions", List.of(questionDetails)); // 리스트 형태로 질문 추가

            return ResponseEntity.ok(response);
        }

        // 질문이 없으면 404 반환
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 1:1 문의 작성
    @PostMapping("/api/questions/create")
    public ResponseEntity<Map<String, String>> createQuestion(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @RequestParam("title") String title,
                                                              @RequestParam("content") String content,
                                                              @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        String studentNumber = userDetails.getUsername();
        User user = userService.findUserByStudentNumber(studentNumber);

        // 최대 3개의 이미지 업로드 처리
        String imageUrl = null;
        String imageUrl2 = null;
        String imageUrl3 = null;

        if (images != null && !images.isEmpty()) {
            if (images.size() > 0) imageUrl = s3Service.uploadFile(images.get(0));
            if (images.size() > 1) imageUrl2 = s3Service.uploadFile(images.get(1));
            if (images.size() > 2) imageUrl3 = s3Service.uploadFile(images.get(2));
        }

        // QuestionDto 생성
        QuestionDto dto = QuestionDto.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .imageUrl2(imageUrl2)
                .imageUrl3(imageUrl3)
                .date(LocalDateTime.now())
                .state(false)
                .build();

        questionService.createQuestion(dto, user);

        // 응답 메시지 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "문의가 성공적으로 등록되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 1:1 문의 수정
    @PutMapping("/api/questions/{id}/update")
    public ResponseEntity<Map<String, String>> updateQuestion(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 질문 조회
        Optional<QuestionDto> question = questionService.getQuestionById(id);
        if (question.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 작성자 검증
        String loggedInStudentNumber = userDetails.getUsername();
        String questionOwnerStudentNumber = question.get().getUserStudentNumber();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        if (!loggedInStudentNumber.equals(questionOwnerStudentNumber) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 거부
        }

        // 기존 이미지 유지 또는 새 이미지 업로드 처리
        String imageUrl = question.get().getImageUrl();
        String imageUrl2 = question.get().getImageUrl2();
        String imageUrl3 = question.get().getImageUrl3();

        if (images != null && !images.isEmpty()) {
            if (images.size() > 0) {
                imageUrl = s3Service.uploadFile(images.get(0));
            }
            if (images.size() > 1) {
                imageUrl2 = s3Service.uploadFile(images.get(1));
            }
            if (images.size() > 2) {
                imageUrl3 = s3Service.uploadFile(images.get(2));
            }
        }

        // DTO 업데이트
        QuestionDto updatedDto = QuestionDto.builder()
                .title(title != null ? title : question.get().getTitle())
                .content(content != null ? content : question.get().getContent())
                .imageUrl(imageUrl)
                .imageUrl2(imageUrl2)
                .imageUrl3(imageUrl3)
                .date(question.get().getDate())
                .state(question.get().isState())
                .answer(question.get().getAnswer())
                .userStudentNumber(question.get().getUserStudentNumber())
                .build();

        Optional<QuestionDto> updatedQuestion = questionService.updateQuestion(id, updatedDto);

        if (updatedQuestion.isPresent()) {
            // 성공 메시지 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "문의가 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 1:1 문의 삭제
    @DeleteMapping("/api/questions/{id}/delete")
    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable Long id,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        Optional<QuestionDto> question = questionService.getQuestionById(id);

        // 문의가 없으면 404 응답 반환
        if (question.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 작성자 검증 - 로그인한 사용자가 관리자이거나 글 작성자와 동일한 학번인지 확인
        String loggedInStudentNumber = userDetails.getUsername();
        String questionOwnerStudentNumber = question.get().getUserStudentNumber();

        if (!loggedInStudentNumber.equals(questionOwnerStudentNumber) &&
                !userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 거부
        }

        // 작성자가 맞거나 관리자일 경우 삭제 진행
        questionService.deleteQuestion(id);

        // 응답 메시지 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "문의가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
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
