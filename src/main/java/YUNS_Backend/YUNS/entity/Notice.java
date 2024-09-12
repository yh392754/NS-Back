package YUNS_Backend.YUNS.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noticeId", updatable = false, nullable = false)
    private Long noticeId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_number", referencedColumnName = "student_number", nullable = false)
    private User user;

    // 필드를 업데이트하여 새로운 Notice 객체를 반환하는 메서드
    public Notice updateNotice(String title, String content, LocalDateTime date) {
        return Notice.builder()
                .noticeId(this.noticeId)  // 기존 ID 유지
                .title(title)             // 업데이트된 제목
                .content(content)         // 업데이트된 내용
                .date(date)               // 업데이트된 날짜
                .user(this.user)          // 기존 User 유지
                .build();
    }
}

