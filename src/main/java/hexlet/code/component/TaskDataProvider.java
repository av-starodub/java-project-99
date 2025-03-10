package hexlet.code.component;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public final class TaskDataProvider {

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    public User getUserById(Long userId) {
        if (nonNull(userId)) {
            return userRepository.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException("User with id=%d not found".formatted(userId)));
        } else {
            return null;
        }
    }

    public TaskStatus getTaskStatusBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug).orElseThrow(
                () -> new ResourceNotFoundException("TaskStatus with slug=%s not found".formatted(slug))
        );
    }

    public Set<Label> getLabelsByIds(List<Long> ids) {
        return nonNull(ids) ? Set.copyOf(labelRepository.findAllById(ids)) : Collections.emptySet();
    }

}
