package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.RentalDto;
import YUNS_Backend.YUNS.entity.RentalStatus;
import YUNS_Backend.YUNS.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/rentals")
public class RentalController {

    private final RentalService rentalService;

    // 전체, 사용 가능, 대여 중, 예약 중 선택 조회
    @GetMapping("/read")
    public ResponseEntity<List<RentalDto>> getRentalStatus(
            @RequestParam(required = false) RentalStatus status) {
        List<RentalDto> rentalList = rentalService.getRentalStatus(status);
        return ResponseEntity.ok(rentalList);
    }

    // 대여 현황 수정
    @PutMapping("/{rentalId}/update")
    public ResponseEntity<Object> updateRentalStatus(@PathVariable Long rentalId,
                                                     @RequestBody Map<String, String> request) {
        RentalStatus status = RentalStatus.valueOf(request.get("status").toUpperCase());
        rentalService.updateRentalStatus(rentalId, status);
        return ResponseEntity.ok(Map.of("message", "대여 현황이 성공적으로 수정되었습니다."));
    }

    // 대여 현황 삭제
    @DeleteMapping("/{rentalId}/delete")
    public ResponseEntity<Object> deleteRental(@PathVariable Long rentalId) {
        rentalService.deleteRental(rentalId);
        return ResponseEntity.ok(Map.of("message", "대여 현황이 성공적으로 삭제되었습니다."));
    }
}
