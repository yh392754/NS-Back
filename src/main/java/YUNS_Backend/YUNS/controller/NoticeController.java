package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.entity.Notice;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.service.NoticeService;
import YUNS_Backend.YUNS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/admin/notices/create")
    public ResponseEntity<Notice> createNotice(@RequestBody Notice notice, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.findUserByStudentNumber(currentUser.getUsername());
        Notice newNotice = Notice.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .date(LocalDateTime.now())
                .user(user)
                .build();
        return ResponseEntity.ok(noticeService.createNotice(newNotice));
    }


}
