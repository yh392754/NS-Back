package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NotebookDetailDto;
import YUNS_Backend.YUNS.dto.NotebookRegistRequestDto;
import YUNS_Backend.YUNS.dto.NotebookFilterDto;
import YUNS_Backend.YUNS.dto.NotebookListDto;
import YUNS_Backend.YUNS.entity.Notebook;
import YUNS_Backend.YUNS.entity.Rental;
import YUNS_Backend.YUNS.entity.RentalStatus;
import YUNS_Backend.YUNS.repository.NotebookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class NotebookService {

    final NotebookRepository notebookRepository;

    public Long saveNotebook(NotebookRegistRequestDto notebookRegistRequestDto, String imgUrl){
        Notebook notebook = Notebook.createNotebook(notebookRegistRequestDto, imgUrl);
        notebookRepository.save(notebook);

        return notebook.getNotebookId();
    }

    public void updateNotebook(NotebookRegistRequestDto notebookRegistRequestDto, String imgUrl, Long notebookId){
        Notebook notebook = notebookRepository.findByNotebookId(notebookId).orElseThrow(EntityNotFoundException::new);
        notebook.updateNotebook(notebookRegistRequestDto, imgUrl);
    }

    public void deleteNotebook(Long notebookId){
        Notebook notebook = notebookRepository.findByNotebookId(notebookId).orElseThrow(EntityNotFoundException::new);
        notebookRepository.delete(notebook);
    }

    @Transactional(readOnly = true)
    public Page<NotebookListDto> getList(NotebookFilterDto notebookFilterDto, Pageable pageable) {
        return notebookRepository.getNotebookListPage(notebookFilterDto, pageable);
    }

    @Transactional(readOnly = true)
    public Set<Integer> getSize(){
        return notebookRepository.findDistinctSize();
    }

    @Transactional(readOnly = true)
    public Set<String> getModel(){
        return notebookRepository.findDistinctModel();
    }

    @Transactional(readOnly = true)
    public NotebookDetailDto getNotebookDetail(Long id){
        Notebook notebook = notebookRepository.findByNotebookId(id)
                .orElseThrow(EntityNotFoundException::new);

        NotebookDetailDto notebookDetailDto = NotebookDetailDto.builder()
                .id(notebook.getNotebookId())
                .model(notebook.getModel())
                .manufactureDate(notebook.getManufactureDate())
                .os(notebook.getOperatingSystem())
                .rentalStatus(notebook.getRentalStatus().toString())
                .imgUrl(notebook.getNotebookImgUrl())
                .size(notebook.getSize())
                .build();

        return notebookDetailDto;
    }

    @Transactional(readOnly = true)
    public Page<NotebookListDto> getNotebooksByRentalStatus(RentalStatus rentalStatus, Pageable pageable) {
        Page<Notebook> notebooks = notebookRepository.findByRentalStatus(rentalStatus, pageable);
        return notebooks.map(notebook -> {
            // 최신 Rental 정보 가져오기 (예시로 첫 번째 렌탈 정보만 가져옴)
            Rental latestRental = notebook.getRentals().isEmpty() ? null : notebook.getRentals().get(0);
            LocalDate rentalStartDate = latestRental != null ? latestRental.getStartDate() : null;
            LocalDate rentalEndDate = latestRental != null ? latestRental.getEndDate() : null;

            // 대여한 User 정보 가져오기
            String renterName = latestRental != null ? latestRental.getUser().getName() : null;
            String renterEmail = latestRental != null ? latestRental.getUser().getEmail() : null;

            return new NotebookListDto(
                    notebook.getNotebookId(),
                    notebook.getModel(),
                    notebook.getRentalStatus(),
                    notebook.getSize(),
                    notebook.getOperatingSystem(),
                    rentalStartDate,
                    rentalEndDate,
                    renterName
            );
        });
    }

    @Transactional
    public void updateRentalStatus(Long notebookId, RentalStatus rentalStatus) {
        Notebook notebook = notebookRepository.findByNotebookId(notebookId)
                .orElseThrow(() -> new EntityNotFoundException("Notebook not found with id: " + notebookId));

        notebook.updateRentalStatus(rentalStatus);
        notebookRepository.save(notebook);
    }
}