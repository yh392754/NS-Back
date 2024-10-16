package YUNS_Backend.YUNS.repository;

import YUNS_Backend.YUNS.dto.NotebookFilterDto;
import YUNS_Backend.YUNS.dto.NotebookListDto;
import YUNS_Backend.YUNS.dto.QNotebookListDto;
import YUNS_Backend.YUNS.entity.QNotebook;
import YUNS_Backend.YUNS.entity.RentalStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class NotebookRepositoryCustomImpl implements NotebookRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public NotebookRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<NotebookListDto> getNotebookListPage(NotebookFilterDto notebookFilterDto, Pageable pageable) {
        QNotebook notebook = QNotebook.notebook;

        QueryResults<NotebookListDto> results = queryFactory
                .select(
                        new QNotebookListDto(notebook.notebookId, notebook.model, notebook.rentalStatus, notebook.operatingSystem)
                )
                .from(notebook)
                .where(filterBy(notebookFilterDto.getFilterBy(), notebookFilterDto.getSelected()),
                        RentalStatusEq(notebookFilterDto.isOnlyAvailable()))
                .orderBy(notebook.notebookId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<NotebookListDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression RentalStatusEq(boolean onlyAvailable){
        return onlyAvailable == true ? QNotebook.notebook.rentalStatus.eq(RentalStatus.AVAILABLE) : null;
    }

    private BooleanExpression filterBy(String filter, String selected){

        if(filter.equals("size")){
            return QNotebook.notebook.size.eq(Integer.valueOf(selected));
        }else if(filter.equals("model")){
            return QNotebook.notebook.model.eq(selected);
        }

        return null;
    }
}
