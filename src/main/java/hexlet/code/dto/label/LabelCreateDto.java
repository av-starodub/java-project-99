package hexlet.code.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class LabelCreateDto {

    public static final int MIN_NAME_LENGTH = 3;

    public static final int MAX_NAME_LENGTH = 1000;

    public static final String NAME_SIZE_ERROR_MESSAGE =
            "Name must be from " + MIN_NAME_LENGTH + " to " + MAX_NAME_LENGTH + " characters long";

    @NotBlank(message = "Name is required")
    @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

}
