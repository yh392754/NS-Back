package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.RentalDto;
import YUNS_Backend.YUNS.entity.Rental;
import YUNS_Backend.YUNS.entity.RentalStatus;
import YUNS_Backend.YUNS.repository.RentalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    // 대여 현황 조회
    public List<RentalDto> getRentalStatus(RentalStatus status) {
        List<Rental> rentals;
        if (status != null) {
            rentals = rentalRepository.findByStatus(status);
        } else {
            rentals = rentalRepository.findAll();
        }

        return rentals.stream()
                .map(RentalDto::new)
                .collect(Collectors.toList());
    }

    // 대여 현황 수정
    public void updateRentalStatus(Long rentalId, RentalStatus status) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("대여 정보가 존재하지 않습니다."));

        rental.updateStatus(status);  // 상태 변경 전용 메서드 호출
        rentalRepository.save(rental); // 변경된 엔티티 저장
    }

    // 대여 현황 삭제
    public void deleteRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("대여 정보가 존재하지 않습니다."));
        rentalRepository.delete(rental);
    }
}
