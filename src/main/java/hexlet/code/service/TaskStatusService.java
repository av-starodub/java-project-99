package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDto;
import hexlet.code.dto.status.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class TaskStatusService {

    private final TaskStatusRepository repo;

    public TaskStatus create(TaskStatusCreateDto taskStatus) {
        var status = TaskStatus.builder()
                .name(taskStatus.getName())
                .slug(taskStatus.getSlug())
                .build();
        return repo.save(status);
    }

    public Optional<TaskStatus> getById(String id) {
        return repo.findBySlug(id);
    }

    public Optional<TaskStatus> getBySlug(String slug) {
        return repo.findBySlug(slug);
    }

    public List<TaskStatus> getAll(String id) {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Optional<TaskStatus> update(TaskStatusUpdateDto updateDto, Long id) {
        return repo.findById(id)
                .map(taskStatus -> updateData(taskStatus, updateDto))
                .map(repo::save);
    }

    private TaskStatus updateData(TaskStatus taskStatus, TaskStatusUpdateDto updateDto) {
        return TaskStatus.builder()
                .id(taskStatus.getId())
                .name(updateDto.getName().orElse(taskStatus.getName()))
                .slug(updateDto.getSlug().orElse(taskStatus.getSlug()))
                .createdAt(taskStatus.getCreatedAt())
                .build();
    }

}
