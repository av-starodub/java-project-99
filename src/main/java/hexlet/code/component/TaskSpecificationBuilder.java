package hexlet.code.component;

import hexlet.code.dto.task.TaskFilterDto;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public final class TaskSpecificationBuilder {

    public Specification<Task> build(TaskFilterDto filter) {
        return withTitleContains(filter.getTitleCont())
                .and(withAssignee(filter.getAssigneeId()))
                .and(withStatus(filter.getStatus()))
                .and(withLabel(filter.getLabelId()));
    }

    private Specification<Task> withTitleContains(String substring) {
        return ((root, query, cb) ->
                isNull(substring)
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + substring.toLowerCase() + "%"));

    }

    private Specification<Task> withAssignee(Long assigneeId) {
        return (root, query, cb) ->
                isNull(assigneeId)
                        ? cb.conjunction()
                        : cb.equal(root.join("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String statusSlug) {
        return (root, query, cb) ->
                isNull(statusSlug)
                        ? cb.conjunction()
                        : cb.equal(root.join("taskStatus", JoinType.INNER).get("slug"), statusSlug);
    }

    private Specification<Task> withLabel(Long labelId) {
        return (root, query, cb) ->
                isNull(labelId)
                        ? cb.conjunction()
                        : cb.equal(root.join("labels", JoinType.INNER).get("id"), labelId);
    }

}
