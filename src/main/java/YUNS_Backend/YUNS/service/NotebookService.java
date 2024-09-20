package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NotebookDto;
import YUNS_Backend.YUNS.entity.Notebook;
import YUNS_Backend.YUNS.repository.NotebookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}