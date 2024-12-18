package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "task_statuses")
public final class TaskStatus {

    public static final int NAME_MIN_LENGTH = 1;

    public static final int SLUG_MIN_LENGTH = 1;

    public static final String NAME_SIZE_ERROR_MESSAGE = "Name must be at least " + NAME_MIN_LENGTH + " character long";

    public static final String SLUG_SIZE_ERROR_MESSAGE = "Slug must be at least " + SLUG_MIN_LENGTH + " character long";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "TaskStatus{id=%d, name=%s, slug=%s, createdAt=%s}".formatted(id, name, slug, createdAt);
    }
}
