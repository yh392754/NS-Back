package YUNS_Backend.YUNS.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RentalDto {

    @Getter
    @Builder
    public static class RentalRequest {
        private Long reservationId;
        private String startDate;
        private String endDate;
        private Long userId;
        private String name;
        private Long notebookId;
    }

    @Getter
    @Builder
    public static class RentalResponse {
        private List<RentalRequest> rentalRequests;
    }
}
