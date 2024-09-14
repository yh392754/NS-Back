package YUNS_Backend.YUNS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String title;
    private String content;
    private LocalDateTime date;
    private boolean state;
    private String answer;
    private String imageUrl;  // 이미지 URL
    private String userStudentNumber;  // 작성자의 학번 추가
}