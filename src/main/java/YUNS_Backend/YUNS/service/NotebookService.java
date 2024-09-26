package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NotebookDto;
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

    public Long saveNotebook(NotebookDto notebookDto, String imgUrl){
        Notebook notebook = Notebook.createNotebook(notebookDto, imgUrl);
        notebookRepository.save(notebook);

        return notebook.getNotebookId();
    }

    public void updateNotebook(NotebookDto notebookDto, String imgUrl, Long notebookId){
        Notebook notebook = notebookRepository.findByNotebookId(notebookId).orElseThrow(EntityNotFoundException::new);
        notebook.updateNotebook(notebookDto, imgUrl);
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
}