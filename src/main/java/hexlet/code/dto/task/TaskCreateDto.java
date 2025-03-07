package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TaskCreateDto {

    @NotBlank
    private String title;

    private Long index;

    private String content;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotNull
    private String status;

    private List<Long> taskLabelIds;
}
