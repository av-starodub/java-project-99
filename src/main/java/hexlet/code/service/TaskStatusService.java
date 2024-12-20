package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDto;
import hexlet.code.dto.status.TaskStatusUpdateDto;
import hexlet.code.exception.UniquenessViolationException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class TaskStatusService {

    private final TaskStatusRepository repo;

    public TaskStatus create(TaskStatusCreateDto taskStatus) {
        var name = taskStatus.getName();
        var slug = taskStatus.getSlug();

        var errorDetails = new ArrayList<String>();
        validateName(name, errorDetails);
        validateSlug(slug, errorDetails);

        if (!errorDetails.isEmpty()) {
            throw new UniquenessViolationException(errorDetails);
        }

        var status = TaskStatus.builder()
                .name(name)
                .slug(slug)
                .build();

        return repo.save(status);
    }

    public Optional<TaskStatus> getById(Long id) {
        return repo.findById(id);
    }

    public Optional<TaskStatus> getBySlug(String slug) {
        return repo.findBySlug(slug);
    }

    public List<TaskStatus> getAll() {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Optional<TaskStatus> update(Long id, TaskStatusUpdateDto updateDto) {
        var errorDetails = new ArrayList<String>();
        updateDto.getName().ifPresent(name -> validateName(name, errorDetails));
        updateDto.getSlug().ifPresent(slug -> validateSlug(slug, errorDetails));

        if (!errorDetails.isEmpty()) {
            throw new UniquenessViolationException(errorDetails);
        }

        return getById(id)
                .map(taskStatus -> updateData(taskStatus, updateDto))
                .map(repo::save);
    }

    private void validateName(String name, List<String> errorDetails) {
        if (repo.existsByName(name)) {
            errorDetails.add("Name %s already exist".formatted(name));
        }
    }

    private void validateSlug(String slug, List<String> errorDetails) {
        if (repo.existsBySlug(slug)) {
            errorDetails.add("Slug %s already exist".formatted(slug));
        }
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
