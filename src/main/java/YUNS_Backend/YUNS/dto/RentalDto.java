package YUNS_Backend.YUNS.dto;

import YUNS_Backend.YUNS.entity.RentalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class RentalDto {

    @Getter
    @Builder
    public static class RentalRequest {
        private Long reservationId;
        private String startDate;      // 문자열로 입력받은 시작 날짜
        private String endDate;        // 문자열로 입력받은 종료 날짜
        private Long userId;
        private String name;
        private Long notebookId;
        private String rentalStatus;   // 문자열로 입력받은 대여 상태 (AVAILABLE, RESERVATION, RENTAL)

        // DTO에서 엔티티의 필드값을 생성하도록 처리
        public LocalDate getParsedStartDate() {
            return startDate != null ? LocalDate.parse(startDate) : null;
        }

        public LocalDate getParsedEndDate() {
            return endDate != null ? LocalDate.parse(endDate) : null;
        }

        public RentalStatus getParsedRentalStatus() {
            return rentalStatus != null ? RentalStatus.valueOf(rentalStatus) : null;
        }
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
        private String rentalStatus;
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
