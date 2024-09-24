package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.dto.NotebookFilterDto;
import YUNS_Backend.YUNS.dto.NotebookListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotebookRepositoryCustom {
    Page<NotebookListDto> getNotebookListPage(NotebookFilterDto notebookFilterDto, Pageable pageable);
}
