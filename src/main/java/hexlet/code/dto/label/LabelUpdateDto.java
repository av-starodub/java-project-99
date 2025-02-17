package hexlet.code.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static hexlet.code.model.Label.MAX_NAME_LENGTH;
import static hexlet.code.model.Label.MIN_NAME_LENGTH;

@AllArgsConstructor
@Getter
public final class LabelUpdateDto {

    @NotBlank(message = "Name is required")
    @Size(
            min = MIN_NAME_LENGTH,
            max = MAX_NAME_LENGTH,
            message = "Name must be from " + MIN_NAME_LENGTH + " to " + MAX_NAME_LENGTH + " characters long"
    )
    private String name;

}
