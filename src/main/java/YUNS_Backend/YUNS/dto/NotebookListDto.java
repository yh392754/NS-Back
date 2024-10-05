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

    @QueryProjection
    public NotebookListDto(Long notebookId, String model, RentalStatus rentalStatus) {
        this.notebookId = notebookId;
        this.model = model;
        this.rentalStatus = rentalStatus;
    }


}
