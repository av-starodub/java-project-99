package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TaskResponseDto {

    private Long id;

    private Long index;

    private LocalDateTime createdAt;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String title;

    private String content;

    private String status;

}
