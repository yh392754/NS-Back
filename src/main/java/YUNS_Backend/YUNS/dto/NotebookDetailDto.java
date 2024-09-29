package YUNS_Backend.YUNS.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
public class NotebookDetailDto {

    private String model;
    private LocalDate manufactureDate;
    private String os;
    private MultipartFile image;
    private int size;
}
