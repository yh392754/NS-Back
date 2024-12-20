package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NoticeDto;
import YUNS_Backend.YUNS.entity.Notice;
import YUNS_Backend.YUNS.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    public Page<NoticeDto> getAllNotices(Pageable pageable) {
        Page<Notice> notices = noticeRepository.findAll(pageable);

        // content 필드를 제외하고 NoticeDto로 변환
        return notices.map(notice -> NoticeDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .date(notice.getDate())
                .build());
    }
    public NoticeDto createNotice(Notice notice) {
        Notice savedNotice = noticeRepository.save(notice);
        return convertToDto(savedNotice);
    }

    public NoticeDto updateNotice(Long id, String title, String content) {
        Optional<Notice> noticeOptional = noticeRepository.findById(id);
        if (noticeOptional.isPresent()) {
            Notice notice = noticeOptional.get();
            Notice updatedNotice = notice.updateNotice(title, content);
            Notice savedNotice = noticeRepository.save(updatedNotice);
            return convertToDto(savedNotice);
        }
        return null;
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    public Optional<NoticeDto> getNoticeById(Long id) {
        Optional<Notice> notice = noticeRepository.findById(id);
        return notice.map(this::convertToDto);
    }

    // 엔티티를 DTO로 변환하는 메서드
    private NoticeDto convertToDto(Notice notice) {
        return NoticeDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .date(notice.getDate())
                .build();
    }

    // DTO를 엔티티로 변환하는 메서드
    public Notice convertToEntity(NoticeDto noticeDto) {
        return Notice.builder()
                .noticeId(noticeDto.getNoticeId())
                .title(noticeDto.getTitle())
                .content(noticeDto.getContent())
                .date(noticeDto.getDate() != null ? noticeDto.getDate() : LocalDateTime.now())
                .build();
    }


}
