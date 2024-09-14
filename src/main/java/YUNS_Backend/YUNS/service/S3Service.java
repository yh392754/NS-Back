package YUNS_Backend.YUNS.service;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    // S3에 파일을 업로드하고 URL을 반환
    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();  // 파일 이름에 UUID 추가
        File convertedFile = convertMultiPartToFile(file);  // MultipartFile을 File로 변환
        amazonS3Client.putObject("yunsawsbucket", fileName, convertedFile);  // 파일 업로드
        return amazonS3Client.getUrl("yunsawsbucket", fileName).toString();  // URL 반환
    }

    // MultipartFile을 File로 변환
    private File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }
}
