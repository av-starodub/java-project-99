package hexlet.code.dto.label;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class LabelResponseDto {

    private Long id;

    private String name;

    private LocalDateTime createdAt;

}
