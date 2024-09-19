package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.Notebook;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long> {
}
