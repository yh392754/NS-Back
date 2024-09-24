package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.entity.Notebook;
import YUNS_Backend.YUNS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Repository
public interface NotebookRepository extends JpaRepository<Notebook, Long>, QuerydslPredicateExecutor<Notebook>, NotebookRepositoryCustom {
    Optional<Notebook> findByNotebookId(Long notebookId);

    @Query("SELECT DISTINCT n.size FROM Notebook n ORDER BY n.size ASC")
    Set<Integer> findDistinctSize();

    @Query("SELECT DISTINCT n.model FROM Notebook n ORDER BY n.model ASC")
    Set<String> findDistinctModel();
}
