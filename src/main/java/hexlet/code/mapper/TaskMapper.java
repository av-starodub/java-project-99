package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDto;
import hexlet.code.dto.task.TaskResponseDto;
import hexlet.code.dto.task.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public final class TaskMapper extends AbstractMapper<Task, TaskCreateDto, TaskUpdateDto, TaskResponseDto> {

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

    @Override
    public Task toDomain(TaskCreateDto dto) {
        return Task.builder()
                .name(dto.getTitle())
                .index(dto.getIndex())
                .description(dto.getContent())
                .taskStatus(findStatusBySlug(dto.getStatus()))
                .assignee(dto.getAssigneeId().map(this::findUserById).orElse(null))
                .labels(dto.getTaskLabelIds().map(labelService::getAllByIds).orElse(Collections.emptySet()))
                .build();
    }

    @Override
    public TaskResponseDto domainTo(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .index(task.getIndex())
                .createdAt(task.getCreatedAt())
                .assigneeId(task.getAssigneeId())
                .title(task.getName())
                .content(task.getDescription())
                .status(task.getStatusSlug())
                .taskLabelIds(task.getLabelIds())
                .build();
    }

    @Override
    public Task update(Task task, TaskUpdateDto dto) {
        dto.getTitle().ifPresent(task::setName);
        dto.getIndex().ifPresent(task::setIndex);
        dto.getContent().ifPresent(task::setDescription);
        dto.getAssigneeId().ifPresent(userId -> task.setAssignee(findUserById(userId)));
        dto.getStatus().ifPresent(statusSlug -> task.setTaskStatus(findStatusBySlug(statusSlug)));
        dto.getLabelIds().ifPresent(labelIds -> task.setLabels(labelService.getAllByIds(labelIds)));
        return task;
    }

    private User findUserById(Long id) {
        return userService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id=%d not found".formatted(id)));
    }

    private TaskStatus findStatusBySlug(String slug) {
        return taskStatusService.getBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with slug=%s not found".formatted(slug)));
    }

}
