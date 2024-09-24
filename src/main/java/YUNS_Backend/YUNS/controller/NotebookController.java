package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.NotebookDto;
import YUNS_Backend.YUNS.dto.NotebookFilterDto;
import YUNS_Backend.YUNS.dto.NotebookListDto;
import YUNS_Backend.YUNS.service.NotebookService;
import YUNS_Backend.YUNS.service.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value = "/api/admin/notebooks/create")
    public ResponseEntity<Object> register(@RequestBody NotebookDto notebookDto){

        String imageUrl = null;
        if(notebookDto.getImage() != null && !notebookDto.getImage().isEmpty()){
            imageUrl = s3Service.uploadFile(notebookDto.getImage());
        }

        Long notebookId = notebookService.saveNotebook(notebookDto, imageUrl);

        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 등록이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/api/admin/notebooks/{notebookId}/update")
    public ResponseEntity<Object> register(@PathVariable("notebookId") Long notebookId, @RequestBody NotebookDto notebookDto) {

        String imageUrl = null;
        Map<String, String> response = new HashMap<>();

        if(notebookDto.getImage() != null && !notebookDto.getImage().isEmpty()){
            imageUrl = s3Service.uploadFile(notebookDto.getImage());
        }

        try{
            notebookService.updateNotebook(notebookDto, imageUrl, notebookId);
        }catch (EntityNotFoundException e){
            response.put("message", "notebookId 정보가 유효하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("message", "성공적으로 수정이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/api/admin/notebooks/{notebookId}/delete")
    public ResponseEntity<Object> register(@PathVariable("notebookId") Long notebookId) {
        Map<String, String> response = new HashMap<>();

        try{
            notebookService.deleteNotebook(notebookId);
        }catch (EntityNotFoundException e){
            response.put("message", "notebookId 정보가 유효하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("message", "성공적으로 삭제가 완료되었습니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = {"api/notebooks/read", "api/notebooks/read/{page}"})
    public ResponseEntity<Object> register(NotebookFilterDto notebookFilterDto, @PathVariable("page") Optional<Integer> page) {
        System.out.println("NotebookFilterDto :"+notebookFilterDto.getFilterBy());
        System.out.println("NotebookFilterDto :"+notebookFilterDto.getSelectd());
        System.out.println("NotebookFilterDto :"+notebookFilterDto.isOnlyAvailable());

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
}
