package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDto;
import hexlet.code.dto.task.TaskUpdateDto;
import hexlet.code.exception.UniquenessViolationException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public Task create(TaskCreateDto createDto) {
        var newTask = taskMapper.toDomain(createDto);
        return taskRepository.save(newTask);
    }

    public Optional<Task> getById(Long id) {
        return taskRepository.findWithRelationsById(id);
    }

    public List<Task> getAll() {
        return taskRepository.findAllWithEagerRelationships();
    }

    public Optional<Task> update(Long id, TaskUpdateDto updateDto) {
        updateDto.getIndex().ifPresent(index -> {
            if (taskRepository.existsByIndex(index)) {
                throw new UniquenessViolationException(List.of("Index %d already exists".formatted(index)));
            }
        });

        return taskRepository.findById(id)
                .map(task -> taskMapper.update(task, updateDto))
                .map(taskRepository::save);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

}
