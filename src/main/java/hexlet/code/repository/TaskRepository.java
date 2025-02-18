package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT CASE WHEN EXISTS "
            + "(SELECT 1 FROM Task t WHERE t.assignee.id = :userId) THEN true ELSE false END"
    )
    boolean existsByAssigneeId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN EXISTS "
            + "(SELECT 1 FROM Task t WHERE t.taskStatus.id = :taskStatusId) THEN true ELSE false END"
    )
    boolean existsByTaskStatusId(@Param("taskStatusId") Long taskStatusId);

    @Query("SELECT CASE WHEN EXISTS "
            + "(SELECT 1 FROM Task t JOIN t.labels l WHERE l.id = :labelId) THEN true ELSE false END")
    boolean existsByLabelId(@Param("labelId") Long labelId);

    boolean existsByIndex(Long index);

    @Query("SELECT t FROM Task t")
    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    List<Task> findAllWithEagerRelationships();

    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    Optional<Task> findWithRelationsById(Long id);

}
