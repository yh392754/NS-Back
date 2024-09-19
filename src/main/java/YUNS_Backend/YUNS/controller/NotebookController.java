package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.NotebookDto;
import YUNS_Backend.YUNS.service.NotebookService;
import YUNS_Backend.YUNS.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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



        Map<String, String> response = new HashMap<>();
        response.put("message", "성공적으로 등록이 완료되었습니다.");

        return ResponseEntity.ok(response);
    }
}
