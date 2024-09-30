package YUNS_Backend.YUNS.controller;

import YUNS_Backend.YUNS.dto.RentalDto.RentalRequest;
import YUNS_Backend.YUNS.dto.RentalDto.RentalResponse;
import YUNS_Backend.YUNS.entity.Rental;
import YUNS_Backend.YUNS.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/request/rental")
@RequiredArgsConstructor
public class RentalController {

    private final RentalRepository rentalRepository;

    @GetMapping
    public RentalResponse getRentalRequests() {
        List<Rental> rentals = rentalRepository.findAll();

        List<RentalRequest> rentalRequests = rentals.stream()
                .map(rental -> RentalRequest.builder()
                        .reservationId(rental.getRentalId()) // Reservation ID와 Rental ID를 동일하게 사용
                        .startDate(rental.getStartDate().toString())
                        .endDate(rental.getEndDate().toString())
                        .userId(rental.getUser().getUserId())
                        .name(rental.getUser().getName())
                        .notebookId(rental.getNotebook().getNotebookId())
                        .build())
                .collect(Collectors.toList());

        return RentalResponse.builder()
                .rentalRequests(rentalRequests)
                .build();
    }
}
