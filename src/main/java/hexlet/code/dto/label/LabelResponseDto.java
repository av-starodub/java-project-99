package hexlet.code.dto.label;

import com.fasterxml.jackson.annotation.JsonFormat;
import hexlet.code.dto.base.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public final class LabelResponseDto extends ResponseDto {

    private Long id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

}
