package hexlet.code.component;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class TaskDataProvider {

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User with id=%d not found".formatted(userId))
        );
    }
    public TaskStatus getTaskStatusBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug).orElseThrow(
                () -> new ResourceNotFoundException("TaskStatus with slug=%s not found".formatted(slug))
        );
    }
}
