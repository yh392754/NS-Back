package YUNS_Backend.YUNS.dto;

import YUNS_Backend.YUNS.entity.RentalStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NotebookListDto {
    private long notebookId;
    private String model;
    private RentalStatus rentalStatus;
    private String os;

    @QueryProjection
    public NotebookListDto(Long notebookId, String model, RentalStatus rentalStatus, String os) {
        this.notebookId = notebookId;
        this.model = model;
        this.rentalStatus = rentalStatus;
        this.os = os;
    }


}
