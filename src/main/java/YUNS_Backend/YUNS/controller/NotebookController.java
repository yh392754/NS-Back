package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.NotebookDetailDto;
import YUNS_Backend.YUNS.dto.NotebookRegistRequestDto;
import YUNS_Backend.YUNS.dto.NotebookFilterDto;
import YUNS_Backend.YUNS.dto.NotebookListDto;
import YUNS_Backend.YUNS.entity.RentalStatus;
import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.exception.ErrorCode;
import YUNS_Backend.YUNS.service.NotebookService;
import YUNS_Backend.YUNS.service.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class NotebookController {

    private final S3Service s3Service;
    private final NotebookService notebookService;

    @PostMapping(value = " ")
    public ResponseEntity<Object> notebookCreate(@RequestBody NotebookRegistRequestDto notebookRegistRequestDto){

        String imageUrl = null;
        if(notebookRegistRequestDto.getImage() != null && !notebookRegistRequestDto.getImage().isEmpty()){
            imageUrl = s3Service.uploadFile(notebookRegistRequestDto.getImage());
        }

        Long notebookId = notebookService.saveNotebook(notebookRegistRequestDto, imageUrl);

        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 등록이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/api/admin/notebooks/{notebookId}/update")
    public ResponseEntity<Object> notebookUpdate(@PathVariable("notebookId") Long notebookId, @RequestBody NotebookRegistRequestDto notebookRegistRequestDto) {

        String imageUrl = null;

        if(notebookRegistRequestDto.getImage() != null && !notebookRegistRequestDto.getImage().isEmpty()){
            imageUrl = s3Service.uploadFile(notebookRegistRequestDto.getImage());
        }

        try{
            notebookService.updateNotebook(notebookRegistRequestDto, imageUrl, notebookId);
        }catch (EntityNotFoundException e){
            throw new CustomException(ErrorCode.NOTEBOOK_NOT_FOUND) ;
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 수정이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/api/admin/notebooks/{notebookId}/delete")
    public ResponseEntity<Object> notebookDelete(@PathVariable("notebookId") Long notebookId) {
        Map<String, String> response = new HashMap<>();

        try{
            notebookService.deleteNotebook(notebookId);
        }catch (EntityNotFoundException e){
            throw new CustomException(ErrorCode.NOTEBOOK_NOT_FOUND) ;
        }

        response.put("message", "성공적으로 삭제가 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = {"api/notebooks/read", "api/notebooks/read/{page}"})
    public ResponseEntity<Object> getNotebookPage(NotebookFilterDto notebookFilterDto, @PathVariable("page") Optional<Integer> page) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        Page<NotebookListDto> notebookList = notebookService.getList(notebookFilterDto, pageable);

        return ResponseEntity.ok(notebookList);
    }

    @GetMapping(value = "api/notebooks/size")
    public ResponseEntity<Object> getSize() {
        Set<Integer> sizeSet = notebookService.getSize();
        return ResponseEntity.ok(sizeSet);
    }

    @GetMapping(value = "api/notebooks/model")
    public ResponseEntity<Object> getModel() {
        Set<String> modelSet = notebookService.getModel();
        return ResponseEntity.ok(modelSet);
    }

    //노트북 상세 조회
    @GetMapping(value = "api/notebooks/{id}/read")
    public ResponseEntity<Object> getDetailNotebook(@PathVariable("id") Long id){

        NotebookDetailDto notebookDetailDto = null;

        try {
            notebookDetailDto = notebookService.getNotebookDetail(id);
        }catch (EntityNotFoundException e){
            throw new CustomException(ErrorCode.NOTEBOOK_NOT_FOUND);
        }

        return ResponseEntity.ok(notebookDetailDto);
    }


    //stauts 파라미터
    @GetMapping("/api/admin/rentals/read")
    public ResponseEntity<Page<NotebookListDto>> getNotebooksByRentalStatus(
            @RequestParam("status") RentalStatus rentalStatus,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        Page<NotebookListDto> notebooks = notebookService.getNotebooksByRentalStatus(rentalStatus, pageable);

        return ResponseEntity.ok(notebooks);
    }


    //rentalStatus 파라미터
    @PutMapping("/api/admin/rentals/{notebookId}/update")
    public ResponseEntity<Object> updateRentalStatus(@PathVariable Long notebookId, @RequestParam RentalStatus rentalStatus) {

        try {
            notebookService.updateRentalStatus(notebookId, rentalStatus);
        } catch (EntityNotFoundException e) {
            throw new CustomException(ErrorCode.NOTEBOOK_NOT_FOUND);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "노트북의 대여 상태가 성공적으로 수정되었습니다.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/admin/rentals/{notebookId}/delete")
    public ResponseEntity<Object> deleteNotebook(@PathVariable Long notebookId) {
        Map<String, String> response = new HashMap<>();

        try {
            // 노트북 삭제 메서드 호출
            notebookService.deleteNotebook(notebookId);
            response.put("message", "노트북이 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            // 노트북을 찾지 못한 경우 예외 처리
            throw new CustomException(ErrorCode.NOTEBOOK_NOT_FOUND);
        }

        return ResponseEntity.ok(response);
    }

}
