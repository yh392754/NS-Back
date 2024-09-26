package YUNS_Backend.YUNS.dto;

import YUNS_Backend.YUNS.entity.Rental;
import YUNS_Backend.YUNS.entity.RentalStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RentalDto {

    private Long rentalId;
    private Long notebookId;
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RentalStatus rentalStatus;

    public RentalDto(Rental rental) {
        this.rentalId = rental.getRentalId();
        this.notebookId = rental.getNotebook().getNotebookId();
        this.userId = rental.getUser().getUserId();
        this.startDate = rental.getStartDate();
        this.endDate = rental.getEndDate();
        this.rentalStatus = rental.getStatus();
    }
}
