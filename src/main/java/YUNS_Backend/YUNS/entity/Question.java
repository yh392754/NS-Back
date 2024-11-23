package YUNS_Backend.YUNS.entity;

import YUNS_Backend.YUNS.dto.QuestionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private boolean state;

    @Column(nullable = true)
    private String answer;

    @Column(nullable = true)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;


    public class QuestionMapper {
        public static QuestionDto toDto(Question question) {
            return QuestionDto.builder()
                    .questionId(question.getQuestionId())
                    .title(question.getTitle())
                    .content(question.getContent())
                    .date(question.getDate())
                    .state(question.isState())
                    .answer(question.getAnswer())
                    .imageUrl(question.getImageUrl())
                    .userStudentNumber(question.getUser().getStudentNumber())
                    .build();
        }
    }
}
