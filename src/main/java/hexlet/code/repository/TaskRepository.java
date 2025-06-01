package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT t FROM Task t")
    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    List<Task> findAllWithEagerRelationships();

    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    Optional<Task> findWithRelationsById(Long id);

}
