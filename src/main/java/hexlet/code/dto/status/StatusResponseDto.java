package hexlet.code.dto.status;


import com.fasterxml.jackson.annotation.JsonFormat;
import hexlet.code.dto.base.ResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public final class StatusResponseDto extends ResponseDto {

    private Long id;

    private String name;

    private String slug;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

}
