package hexlet.code.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static hexlet.code.dto.label.LabelCreateDto.MAX_NAME_LENGTH;
import static hexlet.code.dto.label.LabelCreateDto.MIN_NAME_LENGTH;
import static hexlet.code.dto.label.LabelCreateDto.NAME_SIZE_ERROR_MESSAGE;

@AllArgsConstructor
@Getter
public final class LabelUpdateDto {

    @NotBlank(message = "Name is required")
    @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

}
