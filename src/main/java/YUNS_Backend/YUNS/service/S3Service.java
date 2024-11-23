package YUNS_Backend.YUNS.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3에 파일을 업로드하고 URL을 반환하는 메서드
    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();  // 파일 이름에 UUID 추가
        File convertedFile = convertMultiPartToFile(file);  // MultipartFile을 File로 변환
        amazonS3Client.putObject("yuns", fileName, convertedFile);  // 파일 업로드
        return amazonS3Client.getUrl("yuns", fileName).toString();  // URL 반환
    }

    // MultipartFile을 File로 변환하는 메서드
    private File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }

    public void deleteFile(String imageUrl) {
        try {
            String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            key = URLDecoder.decode(key, StandardCharsets.UTF_8.name());
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
