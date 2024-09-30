package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.entity.*;
import YUNS_Backend.YUNS.exception.CustomException;
import YUNS_Backend.YUNS.exception.ErrorCode;
import YUNS_Backend.YUNS.repository.NotebookRepository;
import YUNS_Backend.YUNS.repository.RentalRepository;
import YUNS_Backend.YUNS.repository.ReservationRepository;
import YUNS_Backend.YUNS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final NotebookRepository notebookRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public void rental(Long notebookId, String studentNumber){

        //사용자가 이미 대여중인 노트북이 있다면
        Optional<Rental> rental = rentalRepository.findByStudentNumber(studentNumber);
        if(rental.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_RENTAL);
        }
        //사용자가 이미 대여 신청한 노트북이 있다면
        Optional<Reservation> reservation = reservationRepository.findByStudentNumber(studentNumber);
        if(reservation.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_RENTAL_REQUEST);
        }

        Notebook notebook = notebookRepository.findByNotebookId(notebookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTEBOOK_NOT_FOUND));

        //노트북 RentalStatus 변경
        notebook.updateRentalStatus(RentalStatus.RESERVATION);

        User user = userRepository.findByStudentNumber(studentNumber);
        if(user == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Reservation newReservation = Reservation.builder()
                .requestDate(LocalDate.now())
                .type(Type.RENTAL)
                .notebook(notebook)
                .user(user)
                .build();

        reservationRepository.save(newReservation);
    }

    public void extend(Long notebookId, String studentNumber){

        Optional<Rental> rental = rentalRepository.findByStudentNumber(studentNumber);
        if(rental.isEmpty()){ //사용자가 대여중인 노트북이 없다면
            throw new CustomException(ErrorCode.RENTAL_NOT_FOUND_BY_USER);
        }else if(!rental.get().getNotebook().getNotebookId().equals(notebookId)){ //연장 요청한 노트북이 대여 중인 노트북이 아니라면
            throw new CustomException(ErrorCode.DIFFERENT_NOTEBOOK);
        }

        Notebook notebook = notebookRepository.findByNotebookId(notebookId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTEBOOK_NOT_FOUND));

        User user = userRepository.findByStudentNumber(studentNumber);
        if(user == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Reservation reservation = Reservation.builder()
                .requestDate(LocalDate.now())
                .type(Type.EXTEND)
                .notebook(notebook)
                .user(user)
                .build();

        reservationRepository.save(reservation);
    }
}
