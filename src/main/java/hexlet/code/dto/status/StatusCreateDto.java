package hexlet.code.dto.status;

import hexlet.code.dto.base.CreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class StatusCreateDto extends CreateDto {

    public static final int NAME_MIN_LENGTH = 1;

    public static final int SLUG_MIN_LENGTH = 1;

    public static final String NAME_SIZE_ERROR_MESSAGE = "Name must be at least " + NAME_MIN_LENGTH + " character long";

    public static final String SLUG_SIZE_ERROR_MESSAGE = "Slug must be at least " + SLUG_MIN_LENGTH + " character long";

    @NotBlank(message = "Name is required")
    @Size(min = NAME_MIN_LENGTH, message = NAME_SIZE_ERROR_MESSAGE)
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(min = SLUG_MIN_LENGTH, message = SLUG_SIZE_ERROR_MESSAGE)
    private String slug;

}
