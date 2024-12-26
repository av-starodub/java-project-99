package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Setter;

import java.util.Optional;

@Setter
@Builder
public final class TaskUpdateDto {

    private String title;

    private Long index;

    private String content;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String status;

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

}
