package hexlet.code.service;

import hexlet.code.dto.status.StatusCreateDto;
import hexlet.code.dto.status.StatusUpdateDto;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class TaskStatusService {

    private final TaskStatusRepository repository;

    private final TaskStatusMapper mapper;

    public TaskStatus create(StatusCreateDto createDto) {
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
        repository.deleteById(id);
    }

    public Optional<TaskStatus> update(Long id, StatusUpdateDto updateDto) {
        return getById(id)
                .map(taskStatus -> mapper.update(taskStatus, updateDto))
                .map(repository::save);
    }

}
