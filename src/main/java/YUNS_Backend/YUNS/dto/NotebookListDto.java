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
    private int size;
    private String operatingSystem;
    private LocalDate rentalStartDate; // Rental 시작일 추가
    private LocalDate rentalEndDate;   // Rental 종료일 추가
    private String renterName;
    @QueryProjection
    public NotebookListDto(Long notebookId, String model, RentalStatus rentalStatus) {
        this.notebookId = notebookId;
        this.model = model;
        this.rentalStatus = rentalStatus;
    }

    public NotebookListDto(Long notebookId, String model, RentalStatus rentalStatus, int size, String operatingSystem, LocalDate rentalStartDate, LocalDate rentalEndDate, String renterName) {
        this.notebookId = notebookId;
        this.model = model;
        this.rentalStatus = rentalStatus;
        this.size = size;
        this.operatingSystem = operatingSystem;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.renterName = renterName;
    }
}
