package YUNS_Backend.YUNS.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class NotebookDetailDto {

    private Long id;
    private String model;
    private LocalDate manufactureDate;
    private String os;
    private String rentalStatus;
    private String imgUrl;
    private int size;
}
