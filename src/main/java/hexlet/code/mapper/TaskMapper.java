package hexlet.code.mapper;

import hexlet.code.component.TaskDataProvider;
import hexlet.code.dto.task.TaskCreateDto;
import hexlet.code.dto.task.TaskResponseDto;
import hexlet.code.dto.task.TaskUpdateDto;
import hexlet.code.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class TaskMapper extends AbstractMapper<Task, TaskCreateDto, TaskUpdateDto, TaskResponseDto> {

    private final TaskDataProvider taskDataProvider;

    @Override
    public Task toDomain(TaskCreateDto dto) {
        return Task.builder()
                .name(dto.getTitle())
                .index(dto.getIndex())
                .description(dto.getContent())
                .taskStatus(taskDataProvider.getTaskStatusBySlug(dto.getStatus()))
                .assignee(taskDataProvider.getUserById(dto.getAssigneeId()))
                .labels(taskDataProvider.getLabelsByIds(dto.getLabelIds()))
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
                .labelIds(task.getLabelIds())
                .build();
    }

    @Override
    public Task update(Task task, TaskUpdateDto dto) {
        dto.getTitle().ifPresent(task::setName);
        dto.getIndex().ifPresent(task::setIndex);
        dto.getContent().ifPresent(task::setDescription);
        dto.getAssigneeId().ifPresent(userId -> {
            var user = taskDataProvider.getUserById(userId);
            task.setAssignee(user);
        });
        dto.getStatus().ifPresent(statusSlug -> {
            var taskStatus = taskDataProvider.getTaskStatusBySlug(statusSlug);
            task.setTaskStatus(taskStatus);
        });
        dto.getLabelIds().ifPresent(labelIds -> {
            var taskLabels = taskDataProvider.getLabelsByIds(labelIds);
            task.setLabels(taskLabels);
        });
        return task;
    }

}
