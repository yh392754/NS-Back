package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.ReservationDto;
import YUNS_Backend.YUNS.entity.Reservation;
import YUNS_Backend.YUNS.entity.Type;
import YUNS_Backend.YUNS.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/request")
public class ExtendController {

    private final ReservationRepository reservationRepository;

    @GetMapping("/extend")
    public ResponseEntity<ReservationDto.ReservationResponse> getExtendRequests() {
        List<Reservation> reservations = reservationRepository.findAll();

        List<ReservationDto.ReservationRequest> extendRequests = reservations.stream()
                .filter(reservation -> reservation.getType() == Type.EXTEND)
                .map(reservation -> ReservationDto.ReservationRequest.builder()
                        .reservationId(reservation.getReservationId())
                        .requestDate(reservation.getRequestDate().toString())
                        .userId(reservation.getUser().getUserId())
                        .name(reservation.getUser().getName())
                        .notebookId(reservation.getNotebook().getNotebookId())
                        .build())
                .collect(Collectors.toList());

        ReservationDto.ReservationResponse response = ReservationDto.ReservationResponse.builder()
                .reservationRequests(extendRequests)
                .build();

        return ResponseEntity.ok(response);
    }
}
