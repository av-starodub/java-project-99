package hexlet.code.util;

import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.dto.user.UserUpdateDto;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;

    private Model<UserCreateDto> userData;

    private Model<UserCreateDto> userDataWithoutRequiredFields;

    private Model<UserCreateDto> userDataWithInvalidEmailAndPassword;

    private Model<UserUpdateDto> userUpdatedData;

    @Autowired
    private Faker faker;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    private void init() {

        userModel = Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field(User::getUpdatedAt))
                .set(field(User::getPasswordHash), encoder.encode("password"))
                .supply(field(User::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        userData = Instancio.of(UserCreateDto.class)
                .supply(field(UserCreateDto::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        userDataWithoutRequiredFields = Instancio.of(UserCreateDto.class)
                .ignore(field(UserCreateDto::getEmail))
                .ignore(field(UserCreateDto::getPassword))
                .toModel();

        userDataWithInvalidEmailAndPassword = Instancio.of(UserCreateDto.class)
                .set(field(UserCreateDto::getEmail), "invalid email")
                .set(field(UserCreateDto::getPassword), "12")
                .toModel();

        userUpdatedData = Instancio.of(UserUpdateDto.class)
                .ignore(field(UserUpdateDto::getFirstName))
                .ignore(field(UserUpdateDto::getLastName))
                .set(field(UserUpdateDto::getPassword), encoder.encode("updated_password"))
                .set(field(UserUpdateDto::getEmail), faker.internet().emailAddress())
                .toModel();
    }

}
