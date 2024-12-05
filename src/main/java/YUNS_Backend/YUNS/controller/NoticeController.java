package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.NoticeDto;
import YUNS_Backend.YUNS.entity.Notice;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.service.NoticeService;
import YUNS_Backend.YUNS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/admin/notices/create")
    public ResponseEntity<NoticeDto> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails currentUser) {

        // 현재 로그인된 사용자 정보 가져오기
        User user = userService.findUserByStudentNumber(currentUser.getUsername());
        if (user == null) {
            return ResponseEntity.badRequest().body(null); // 사용자 정보가 없으면 에러 반환
        }

        // NoticeDto 생성
        NoticeDto noticeDto = NoticeDto.builder()
                .title(title)
                .content(content)
                .build();

        // 공지사항 생성
        NoticeDto createdNotice = noticeService.createNotice(noticeDto, images, user);
        return ResponseEntity.ok(createdNotice);
    }



    @PutMapping("/api/admin/notices/{id}/update")
    public ResponseEntity<NoticeDto> updateNotice(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestParam(value = "oldImageUrls", required = false) List<String> oldImageUrls) {

        System.out.println("Updating Notice ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Content: " + content);
        System.out.println("New Images: " + (newImages != null ? newImages.size() : "None"));
        System.out.println("Old Image URLs: " + (oldImageUrls != null ? oldImageUrls.size() : "None"));

        // DTO를 직접 생성
        NoticeDto noticeDto = NoticeDto.builder()
                .title(title)
                .content(content)
                .build();

        // Service 호출
        NoticeDto updatedNotice = noticeService.updateNotice(id, noticeDto, newImages, oldImageUrls);
        return updatedNotice != null ? ResponseEntity.ok(updatedNotice) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/api/admin/notices/{id}/delete")
    public ResponseEntity<String> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }

    @GetMapping("/api/noticeList")
    public ResponseEntity<Page<NoticeDto>> getAllNotices(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "desc") String sortOrder) {
        Pageable pageable = PageRequest.of(page, size, "asc".equalsIgnoreCase(sortOrder)
                ? Sort.by("noticeId").ascending()
                : Sort.by("noticeId").descending());
        Page<NoticeDto> noticeList = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(noticeList);
    }


    @GetMapping("/api/noticeList/{noticeId}")
    public ResponseEntity<NoticeDto> getNoticeById(@PathVariable Long noticeId) {
        Optional<NoticeDto> noticeDto = noticeService.getNoticeById(noticeId);
        return noticeDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

