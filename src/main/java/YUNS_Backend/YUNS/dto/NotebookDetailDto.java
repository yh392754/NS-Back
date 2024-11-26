package YUNS_Backend.YUNS.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class NotebookDetailDto {

    private Long id;
    private String model;
    private String content;
    private String manufactureDate;
    private String os;
    private String rentalStatus;
    private List<String> imgUrl;
    private int size;
}
