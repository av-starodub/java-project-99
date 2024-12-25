package hexlet.code.mapper;

import hexlet.code.dto.status.StatusCreateDto;
import hexlet.code.dto.status.StatusResponseDto;
import hexlet.code.dto.status.StatusUpdateDto;
import hexlet.code.model.TaskStatus;
import org.springframework.stereotype.Service;

@Service
public final class TaskStatusMapper
        extends AbstractMapper<TaskStatus, StatusCreateDto, StatusUpdateDto, StatusResponseDto> {

    @Override
    public TaskStatus toDomain(StatusCreateDto createDto) {
        return TaskStatus.builder()
                .name(createDto.getName())
                .slug(createDto.getSlug())
                .build();
    }

    @Override
    public StatusResponseDto domainTo(TaskStatus taskStatus) {
        return StatusResponseDto.builder()
                .id(taskStatus.getId())
                .name(taskStatus.getName())
                .slug(taskStatus.getSlug())
                .createdAt(taskStatus.getCreatedAt())
                .build();
    }

    @Override
    public TaskStatus update(TaskStatus taskStatus, StatusUpdateDto updateDto) {
        taskStatus.setName(updateDto.getName().orElse(taskStatus.getName()));
        taskStatus.setSlug(updateDto.getSlug().orElse(taskStatus.getSlug()));
        return taskStatus;
    }

}
