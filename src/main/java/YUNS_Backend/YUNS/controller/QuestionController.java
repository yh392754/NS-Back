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

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final S3Service s3Service;

    // 1:1 문의 리스트 조회
    @GetMapping("/api/questions/read")
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    // 2. 1:1 문의 세부 조회 (GET, /api/questions/{id}/read) - 로그인 불필요
    @GetMapping("/api/questions/{id}/read")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        Optional<QuestionDto> question = questionService.getQuestionById(id);
        return question.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. 1:1 문의 작성 (POST, /api/questions/create) - 로그인 필요
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

}
