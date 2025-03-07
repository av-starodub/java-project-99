package hexlet.code.dto.task;

import lombok.Getter;

@Getter
public class TaskFilterDto {

    private String titleCont;

    private Long assigneeId;

    private String status;

    private Long labelId;

}
