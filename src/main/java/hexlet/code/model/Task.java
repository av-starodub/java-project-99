package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tasks")
public final class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private Long index;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TaskStatus taskStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getAssigneeId() {
        return nonNull(assignee) ? assignee.getId() : null;
    }

    public String getStatusSlug() {
        if (isNull(taskStatus)) {
            throw new IllegalStateException("Task status not set");
        }
        return taskStatus.getSlug();
    }

    @Override
    public String toString() {
        return "Task{id=%d, name=%s, index=%s, description=%s, createdAt=%s}"
                .formatted(id, name, index, description, createdAt);
    }

}
