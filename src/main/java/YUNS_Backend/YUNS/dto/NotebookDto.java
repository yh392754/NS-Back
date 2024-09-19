package YUNS_Backend.YUNS.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class NotebookDto {

    private String model;
    private LocalDate manufactureDate;
    private String os;
    private MultipartFile image;
}
