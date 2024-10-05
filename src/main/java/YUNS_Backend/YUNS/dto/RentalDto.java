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
        private Long rentalId;
        private String startDate;
        private String endDate;
        private Long userId;
        private Long notebookId;
    }

    @Getter
    @Builder
    public static class RentalApprovalRequest {
        private Long reservationId;
        private String type; // RENTAL 혹은 EXTEND
    }


    @Getter
    @Builder
    public static class CurrentRentalDto {
        private Long rentalId;       // 대여 기록의 고유 ID
        private Long notebookId;     // 대여된 노트북 ID
        private String startDate;    // 대여 시작 날짜
        private String endDate;      // 반납 예정 날짜
        private String rentalStatus; // 대여 상태 (예: "대여 중", "미반납" 등)
    }

}
