package YUNS_Backend.YUNS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
public class NotebookRegistRequestDto {

    @NotBlank
    private String model;

    @NotNull
    private LocalDate manufactureDate;

    @NotBlank
    private String os;

    private MultipartFile image;

    @NotNull
    private Integer size;
}
