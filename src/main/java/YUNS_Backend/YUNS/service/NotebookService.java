package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NotebookDetailDto;
import YUNS_Backend.YUNS.dto.NotebookRegistRequestDto;
import YUNS_Backend.YUNS.dto.NotebookFilterDto;
import YUNS_Backend.YUNS.dto.NotebookListDto;
import YUNS_Backend.YUNS.entity.Notebook;
import YUNS_Backend.YUNS.repository.NotebookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}