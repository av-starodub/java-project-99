package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.dto.base.UpdateDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

import static hexlet.code.dto.task.TaskCreateDto.LABEL_NULL_ERROR_MESSAGE;

@Setter
@Builder
public final class TaskUpdateDto extends UpdateDto {

    @Pattern(regexp = ".*\\S.*", message = "The name should not consist only of spaces")
    private String title;

    private Long index;

    private String content;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String status;

    private List<@NotNull(message = LABEL_NULL_ERROR_MESSAGE) Long> labelIds;

    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<Long> getIndex() {
        return Optional.ofNullable(index);
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<Long> getAssigneeId() {
        return Optional.ofNullable(assigneeId);
    }

    public Optional<String> getStatus() {
        return Optional.ofNullable(status);
    }

    public Optional<List<Long>> getLabelIds() {
        return Optional.ofNullable(labelIds);
    }

}
