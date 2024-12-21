package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByAssigneeId(Long userId);

    boolean existsByTaskStatusId(Long taskStatusId);

}
