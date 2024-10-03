package YUNS_Backend.YUNS.dto;

import YUNS_Backend.YUNS.entity.RentalStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class NotebookListDto {
    long notebookId;
    String model;
    RentalStatus rentalStatus;
    private int size;
    private String operatingSystem;

    @QueryProjection
    public NotebookListDto(Long notebookId, String model, RentalStatus rentalStatus) {
        this.notebookId = notebookId;
        this.model = model;
        this.rentalStatus = rentalStatus;
    }

    public NotebookListDto(Long notebookId, String model, int size, String operatingSystem) {
        this.notebookId = notebookId;
        this.model = model;
        this.size = size;
        this.operatingSystem = operatingSystem;
    }
}
