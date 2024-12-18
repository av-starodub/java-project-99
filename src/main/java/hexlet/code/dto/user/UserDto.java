package hexlet.code.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserDto {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
