package hexlet.code.util;

import hexlet.code.dto.UserCreateDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    private Model<UserCreateDto> userCreateDto;

    @Autowired
    private Faker faker;


    @PostConstruct
    private void init() {

        userCreateDto = Instancio.of(UserCreateDto.class)
                .supply(Select.field(UserCreateDto::getEmail), () -> faker.internet().emailAddress())
                .toModel();
    }

}
