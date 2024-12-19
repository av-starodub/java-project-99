package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.exception.ApplicationInitializationException;
import hexlet.code.model.DefaultTaskStatusType;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DataInitializer implements ApplicationRunner {

    private final UserService userService;

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    @Override
    public void run(ApplicationArguments args) {
        try {
            var adminData = UserCreateDto.builder()
                    .email("hexlet@example.com")
                    .password("qwerty")
                    .build();
            if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
                userService.create(adminData);
            }

            DefaultTaskStatusType.getAll().stream()
                    .filter(statusType -> taskStatusRepository.findBySlug(statusType.getSlug()).isEmpty())
                    .map(statusType -> TaskStatus.builder()
                            .name(statusType.getName())
                            .slug(statusType.getSlug())
                            .build())
                    .forEach(taskStatusRepository::save);

        } catch (Exception e) {
            throw new ApplicationInitializationException("Failed to init data: %s".formatted(e.getMessage()), e);
        }
    }

}
