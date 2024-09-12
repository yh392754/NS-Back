package YUNS_Backend.YUNS.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoticeDto {
    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime date;
}
