package YUNS_Backend.YUNS.service;

import YUNS_Backend.YUNS.dto.NotebookDto;
import YUNS_Backend.YUNS.entity.Notebook;
import YUNS_Backend.YUNS.repository.NotebookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotebookService {

    final NotebookRepository notebookRepository;

    public Long saveNotebook(NotebookDto notebookDto, String imgUrl){
        Notebook notebook = Notebook.createNotebook(notebookDto, imgUrl);
        notebookRepository.save(notebook);

        return notebook.getNotebookId();
    }
}