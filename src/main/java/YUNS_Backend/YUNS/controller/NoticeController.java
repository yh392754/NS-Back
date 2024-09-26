package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.NoticeDto;
import YUNS_Backend.YUNS.entity.Notice;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.service.NoticeService;
import YUNS_Backend.YUNS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/admin/notices/create")
    public ResponseEntity<NoticeDto> createNotice(@RequestBody NoticeDto noticeDto, @AuthenticationPrincipal UserDetails currentUser) {
      User user = userService.findUserByStudentNumber(currentUser.getUsername());

        Notice newNotice = noticeService.convertToEntity(noticeDto);

        // User 설정을 위해 updateUser 메서드 호출
        newNotice = newNotice.updateUser(user);

         NoticeDto createdNoticeDto = noticeService.createNotice(newNotice);
        return ResponseEntity.ok(createdNoticeDto);
    }

    @PutMapping("/api/admin/notices/{id}/update")
    public ResponseEntity<NoticeDto> updateNotice(@PathVariable Long id, @RequestBody NoticeDto noticeDto) {
        NoticeDto updatedNoticeDto = noticeService.updateNotice(id, noticeDto.getTitle(), noticeDto.getContent());
        return updatedNoticeDto != null ? ResponseEntity.ok(updatedNoticeDto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/admin/notices/{id}/delete")
    public ResponseEntity<String> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok("공지사항이 삭제되었습니다");
    }

    @GetMapping("/api/noticeList")
    public ResponseEntity<Page<NoticeDto>> getAllNotices(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeDto> noticeList = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(noticeList);
    }
    @GetMapping("/api/noticeList/{noticeId}")
    public ResponseEntity<NoticeDto> getNoticeById(@PathVariable Long noticeId) {
        Optional<NoticeDto> noticeDto = noticeService.getNoticeById(noticeId);
        return noticeDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }






}
