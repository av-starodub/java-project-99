package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.dto.base.CreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
public class TaskCreateDto extends CreateDto {

    public static final String LABEL_NULL_ERROR_MESSAGE = "Label id must not be null";

    @NotBlank(message = "Title is required to create a new task")
    private String title;

    private Long index;

    private String content;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank(message = "Status slug is required to create a new task")
    private String status;

    private List<@NotNull(message = LABEL_NULL_ERROR_MESSAGE) Long> taskLabelIds;

    public Optional<Long> getAssigneeId() {
        return Optional.ofNullable(assigneeId);
    }

    public Optional<List<Long>> getTaskLabelIds() {
        return Optional.ofNullable(taskLabelIds);
    }

}
