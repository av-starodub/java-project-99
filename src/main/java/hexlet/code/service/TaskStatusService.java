package hexlet.code.service;

import hexlet.code.dto.status.StatusCreateDto;
import hexlet.code.dto.status.StatusUpdateDto;
import hexlet.code.exception.TaskStatusInUseException;
import hexlet.code.exception.UniquenessViolationException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class TaskStatusService {

    private final TaskStatusRepository repository;

    private final TaskStatusMapper mapper;

    private final TaskRepository taskRepository;

    public TaskStatus create(StatusCreateDto createDto) {
        var name = createDto.getName();
        var slug = createDto.getSlug();

        var errorDetails = new ArrayList<String>();
        validateName(name, errorDetails);
        validateSlug(slug, errorDetails);

        if (!errorDetails.isEmpty()) {
            throw new UniquenessViolationException(errorDetails);
        }

        var status = mapper.toDomain(createDto);
        return repository.save(status);
    }

    public Optional<TaskStatus> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<TaskStatus> getBySlug(String slug) {
        return repository.findBySlug(slug);
    }

    public List<TaskStatus> getAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        if (taskRepository.existsByTaskStatusId(id)) {
            throw new TaskStatusInUseException("Cannot delete. TaskStatus is referenced to one or more tasks.");
        }
        repository.deleteById(id);
    }

    public Optional<TaskStatus> update(Long id, StatusUpdateDto updateDto) {
        var errorDetails = new ArrayList<String>();
        updateDto.getName().ifPresent(name -> validateName(name, errorDetails));
        updateDto.getSlug().ifPresent(slug -> validateSlug(slug, errorDetails));

        if (!errorDetails.isEmpty()) {
            throw new UniquenessViolationException(errorDetails);
        }

        return getById(id)
                .map(taskStatus -> mapper.update(taskStatus, updateDto))
                .map(repository::save);
    }

    private void validateName(String name, List<String> errorDetails) {
        if (repository.existsByName(name)) {
            errorDetails.add("Name %s already exist".formatted(name));
        }
    }

    private void validateSlug(String slug, List<String> errorDetails) {
        if (repository.existsBySlug(slug)) {
            errorDetails.add("Slug %s already exist".formatted(slug));
        }
    }

}
