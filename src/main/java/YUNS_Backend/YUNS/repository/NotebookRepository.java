package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.Notebook;
import YUNS_Backend.YUNS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long>, QuerydslPredicateExecutor<Notebook>, NotebookRepositoryCustom {
    Optional<Notebook> findByNotebookId(Long notebookId);
}
