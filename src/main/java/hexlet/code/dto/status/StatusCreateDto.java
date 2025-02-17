package hexlet.code.dto.status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import static hexlet.code.model.TaskStatus.NAME_MIN_LENGTH;
import static hexlet.code.model.TaskStatus.NAME_SIZE_ERROR_MESSAGE;
import static hexlet.code.model.TaskStatus.SLUG_MIN_LENGTH;
import static hexlet.code.model.TaskStatus.SLUG_SIZE_ERROR_MESSAGE;

@Getter
@Builder
public final class StatusCreateDto {

    @NotBlank(message = "Name is required")
    @Size(min = NAME_MIN_LENGTH, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(min = SLUG_MIN_LENGTH, message = SLUG_SIZE_ERROR_MESSAGE)
    private String slug;

}
