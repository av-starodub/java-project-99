package hexlet.code.dto.status;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public final class TaskStatusDto {

    private Long id;

    private String name;

    private String slug;

    private LocalDateTime createdAt;

}
