package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NoticeDto;
import YUNS_Backend.YUNS.entity.Notice;
import YUNS_Backend.YUNS.entity.User;
import YUNS_Backend.YUNS.repository.NoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import YUNS_Backend.YUNS.entity.NoticeImage;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    private final S3Service s3Service;

    @Autowired
    public NoticeService(S3Service s3Service) {
        this.s3Service =  s3Service;
    }
    public Page<NoticeDto> getAllNotices(Pageable pageable) {
        Page<Notice> notices = noticeRepository.findAll(pageable);

        // content 필드를 제외하고 NoticeDto로 변환
        return notices.map(notice -> NoticeDto.builder()
                .noticeId(notice.getNoticeId()) // id
                .title(notice.getTitle())       // title
                .date(notice.getDate())         // date
                .build());
    }
    public NoticeDto createNotice(NoticeDto noticeDto, List<MultipartFile> images, User user) {
        // 1. 이미지 업로드
        List<String> imageUrls = uploadImagesToS3(images);

        // 2. Notice 엔티티 생성 및 설정
        Notice notice = convertToEntity(noticeDto);
        notice.setUser(user);

        // 3. NoticeImage 생성 및 추가
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                NoticeImage noticeImage = new NoticeImage(notice, imageUrl);
                notice.addImage(noticeImage); // 연관관계 설정
            }
        }
        // 4. Notice 저장
        Notice savedNotice = noticeRepository.save(notice);

        // 5. DTO 반환
        return convertToDto(savedNotice);
    }





    @Transactional
    public NoticeDto updateNotice(Long id, NoticeDto noticeDto, List<MultipartFile> newImages, List<String> oldImageUrls) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isEmpty()) {
            throw new EntityNotFoundException("Notice not found with id: " + id);
        }

        Notice notice = optionalNotice.get();

        // 기존 이미지 삭제
        if (oldImageUrls != null && !oldImageUrls.isEmpty()) {
            for (String oldImageUrl : oldImageUrls) {
                System.out.println("Deleting image: " + oldImageUrl);

                // S3 삭제
                s3Service.deleteFile(oldImageUrl);

                // 엔티티에서 이미지 삭제
                boolean removed = notice.getImages().removeIf(image -> image.getImageUrl().equals(oldImageUrl));
                System.out.println("Removed from Notice entity: " + removed);
            }
        }

        // 새 이미지 업로드
        List<String> newImageUrls = uploadImagesToS3(newImages);
        List<NoticeImage> newNoticeImages = newImageUrls.stream()
                .map(url -> new NoticeImage(notice, url))
                .toList();
        notice.updateImages(newNoticeImages);

        // 공지사항 내용 업데이트
        notice.updateNotice(noticeDto.getTitle(), noticeDto.getContent());

        // 데이터베이스 저장
        Notice savedNotice = noticeRepository.save(notice);

        return convertToDto(savedNotice);
    }







    public void deleteNotice(Long id) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();

            // Delete all associated images from S3
            notice.getImages().forEach(image -> s3Service.deleteFile(image.getImageUrl()));

            noticeRepository.deleteById(id);
        }
    }

    private List<String> uploadImagesToS3(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            imageUrls.add(s3Service.uploadFile(image));
        }
        return imageUrls;
    }

    public Optional<NoticeDto> getNoticeById(Long id) {
        Optional<Notice> notice = noticeRepository.findById(id);
        return notice.map(this::convertToDto);
    }

    private NoticeDto convertToDto(Notice notice) {
        return NoticeDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .date(notice.getDate())
                .imageUrls(notice.getImages() != null ?
                        notice.getImages().stream()
                                .map(NoticeImage::getImageUrl)
                                .toList()
                        : new ArrayList<>())

                .build();
    }


    public Notice convertToEntity(NoticeDto noticeDto) {
        return Notice.builder()
                .noticeId(noticeDto.getNoticeId())
                .title(noticeDto.getTitle())
                .content(noticeDto.getContent())
                .date(noticeDto.getDate() != null ? noticeDto.getDate() : LocalDateTime.now())
                .build();
    }





}
