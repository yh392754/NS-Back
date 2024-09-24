package YUNS_Backend.YUNS.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotebookFilterDto {
    //사용자가 선택한 필터(크기/모델/선택안함)
    String filterBy;
    //사용자가 드롭다운에서 선택한 것
    String selectd;
    //대여가능한 노트북만 보기 선택 여부
    boolean onlyAvailable;

}
