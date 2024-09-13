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

}
