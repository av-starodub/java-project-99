package hexlet.code.dto.label;

import hexlet.code.dto.base.UpdateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static hexlet.code.dto.label.LabelCreateDto.MAX_NAME_LENGTH;
import static hexlet.code.dto.label.LabelCreateDto.MIN_NAME_LENGTH;
import static hexlet.code.dto.label.LabelCreateDto.NAME_SIZE_ERROR_MESSAGE;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public final class LabelUpdateDto extends UpdateDto {

    @NotBlank(message = "Name is required")
    @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

}
