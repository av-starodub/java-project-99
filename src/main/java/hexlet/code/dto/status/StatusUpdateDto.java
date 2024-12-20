package hexlet.code.dto.status;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Optional;

import static hexlet.code.model.TaskStatus.NAME_MIN_LENGTH;
import static hexlet.code.model.TaskStatus.NAME_SIZE_ERROR_MESSAGE;
import static hexlet.code.model.TaskStatus.SLUG_MIN_LENGTH;
import static hexlet.code.model.TaskStatus.SLUG_SIZE_ERROR_MESSAGE;

@Builder
public final class StatusUpdateDto {

    @Size(min = NAME_MIN_LENGTH, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

    @Size(min = SLUG_MIN_LENGTH, message = SLUG_SIZE_ERROR_MESSAGE)
    private String slug;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getSlug() {
        return Optional.ofNullable(slug);
    }

}
