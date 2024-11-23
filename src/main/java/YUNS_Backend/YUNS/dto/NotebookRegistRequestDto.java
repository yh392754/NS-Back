package YUNS_Backend.YUNS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Setter
@Getter
public class NotebookRegistRequestDto {

    @NotBlank
    private String model;

    @NotBlank
    private String manufactureDate;

    @NotBlank
    private String os;

    private List<MultipartFile> images;

    @NotNull
    private Integer size;
}
