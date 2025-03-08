package hexlet.code.dto.label;

import hexlet.code.dto.base.CreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public final class LabelCreateDto extends CreateDto {

    public static final int MIN_NAME_LENGTH = 3;

    public static final int MAX_NAME_LENGTH = 1000;

    public static final String NAME_SIZE_ERROR_MESSAGE =
            "Name must be from " + MIN_NAME_LENGTH + " to " + MAX_NAME_LENGTH + " characters long";

    @NotBlank(message = "Name is required")
    @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

}
