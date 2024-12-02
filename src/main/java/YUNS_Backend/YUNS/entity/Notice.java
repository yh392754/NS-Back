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
    public Notice updateNotice(String title, String content) {
        return Notice.builder()
                .noticeId(this.noticeId)  // 기존 ID 유지
                .title(title != null ? title : this.title)  // title이 null이면 기존 값 유지
                .content(content != null ? content : this.content)  // content가 null이면 기존 값 유지
                .date(this.date)  // date는 기존 값 유지
                .user(this.user)  // 기존 User 유지
                .build();
    }

    // User 업데이트를 위한 별도의 메서드
    public Notice updateUser(User user) {
        return Notice.builder()
                .noticeId(this.noticeId)
                .title(this.title)
                .content(this.content)
                .date(this.date)
                .user(user != null ? user : this.user)  // user가 null이면 기존 값 유지
                .build();
    }
}
