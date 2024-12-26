package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT CASE WHEN EXISTS "
            + "(SELECT 1 FROM Task t WHERE t.assignee.id = :userId) THEN true ELSE false END"
    )
    boolean existsByAssigneeId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN EXISTS "
            + "(SELECT 1 FROM Task t WHERE t.taskStatus.id = :taskStatusId) THEN true ELSE false END"
    )
    boolean existsByTaskStatusId(@Param("taskStatusId") Long taskStatusId);

}
