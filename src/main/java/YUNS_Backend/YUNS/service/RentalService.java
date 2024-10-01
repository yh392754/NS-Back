package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.entity.Rental;
import YUNS_Backend.YUNS.entity.Reservation;
import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.exception.ErrorCode;
import YUNS_Backend.YUNS.repository.RentalRepository;
import YUNS_Backend.YUNS.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;

    // 대여 요청 승인
    @Transactional
    public void approveRentalRequest(Long reservationId, String type) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId)); // 간단한 예외 처리

        if (type.equals("RENTAL")) {
            Rental rental = Rental.builder()
                    .user(reservation.getUser())
                    .notebook(reservation.getNotebook())
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(1)) // 반납예정 1개월
                    .build();

            rentalRepository.save(rental);
        }

        reservationRepository.delete(reservation);
    }
}
