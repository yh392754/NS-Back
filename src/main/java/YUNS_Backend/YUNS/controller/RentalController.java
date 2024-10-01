package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.RentalDto;
import YUNS_Backend.YUNS.dto.RentalDto.RentalRequest;
import YUNS_Backend.YUNS.dto.RentalDto.RentalResponse;
import YUNS_Backend.YUNS.dto.ReservationDto;
import YUNS_Backend.YUNS.entity.Rental;
import YUNS_Backend.YUNS.entity.Reservation;
import YUNS_Backend.YUNS.repository.RentalRepository;
import YUNS_Backend.YUNS.repository.ReservationRepository;
import YUNS_Backend.YUNS.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/request/rental")
@RequiredArgsConstructor
public class RentalController {

    private final ReservationRepository reservationRepository;
    private final RentalService rentalService;

    @GetMapping
    public ReservationDto.ReservationResponse getRentalRequests() {
        List<Reservation> reservations = reservationRepository.findAll();  // 모든 대여 요청 조회

        List<ReservationDto.ReservationRequest> reservationRequests = reservations.stream()
                .map(reservation -> ReservationDto.ReservationRequest.builder()
                        .reservationId(reservation.getReservationId())
                        .requestDate(reservation.getRequestDate().toString())
                        .userId(reservation.getUser().getUserId())
                        .name(reservation.getUser().getName())
                        .notebookId(reservation.getNotebook().getNotebookId())
                        .build())
                .collect(Collectors.toList());

        return ReservationDto.ReservationResponse.builder()
                .reservationRequests(reservationRequests)
                .build();
    }

    @PostMapping("/approve/{reservationId}")
    public ResponseEntity<Object> approveRentalRequest(@PathVariable Long reservationId, @RequestBody RentalDto.RentalApprovalRequest rentalApprovalRequest) {
        rentalService.approveRentalRequest(reservationId, rentalApprovalRequest.getType());

        Map<String, String> response = new HashMap<>();
        response.put("message", "대여 요청 승인이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }


}
