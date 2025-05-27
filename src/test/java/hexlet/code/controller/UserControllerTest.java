package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private User testUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor testUserToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        var testUserData = Instancio.of(modelGenerator.getUserModel()).create();
        testUserToken = jwt().jwt(builder -> builder.subject(testUserData.getEmail()));
        testUser = userRepository.save(testUserData);
    }

    @AfterAll
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should handle valid POST to create new User correctly")
    void checkCreate() throws Exception {
        var inputUserData = Instancio.of(modelGenerator.getUserInputData()).create();

        var request = post("/api/users")
                .with(testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUserData));
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(inputUserData.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(inputUserData.getLastName()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist());

        var newUser = userService.getByEmail(inputUserData.getEmail()).orElse(null);
        assertThat(newUser).isNotNull();
        assertThat(encoder.matches(inputUserData.getPassword(), newUser.getPasswordHash())).isTrue();
        assertThat(newUser)
                .hasFieldOrPropertyWithValue("firstName", inputUserData.getFirstName())
                .hasFieldOrPropertyWithValue("lastName", inputUserData.getLastName())
                .hasFieldOrProperty("createdAt").isNotNull()
                .hasFieldOrProperty("updatedAt").isNotNull();
    }

    @Test
    @DisplayName("Should handle GET to show all existing users correctly")
    void checkGetAllUsers() throws Exception {
        var expectedUsers = userService.getAll();
        var savedUserIdx = expectedUsers.indexOf(testUser);

        Function<String, String> toPath = (key) -> "$[%d].%s".formatted(savedUserIdx, key);

        var request = get("/api/users").with(testUserToken);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedUsers.size()))
                .andExpect(header().string("X-Total-Count", String.valueOf(expectedUsers.size())))
                .andExpect(jsonPath(toPath.apply("id")).value(testUser.getId()))
                .andExpect(jsonPath(toPath.apply("firstName")).value(testUser.getFirstName()))
                .andExpect(jsonPath(toPath.apply("lastName")).value(testUser.getLastName()))
                .andExpect(jsonPath(toPath.apply("email")).value(testUser.getEmail()))
                .andExpect(jsonPath(toPath.apply("createdAt")).isNotEmpty())
                .andExpect(jsonPath(toPath.apply("updatedAt")).isNotEmpty())
                .andExpect(jsonPath(toPath.apply("password")).doesNotExist());
    }

    @Test
    @DisplayName("Should handle GET by ID correctly")
    void checkShowById() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(testUserToken);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Should handle PUT to update only new data correctly")
    void checkUpdateOnlyNewData() throws Exception {
        var updatedUserData = Instancio.of(modelGenerator.getUserUpdatedData()).create();

        var request = put("/api/users/" + testUser.getId())
                .with(testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserData));

        var newEmail = updatedUserData.getEmail().orElse(null);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.email").value(newEmail));

        var updatedUser = userService.getById(testUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull()
                .hasFieldOrPropertyWithValue("firstName", testUser.getFirstName())
                .hasFieldOrPropertyWithValue("lastName", testUser.getLastName())
                .hasFieldOrPropertyWithValue("email", newEmail);

        var newPassword = updatedUserData.getPassword().orElse(null);
        assertThat(encoder.matches(newPassword, updatedUser.getPasswordHash())).isTrue();

        var previousCreatedAtValue = testUser.getCreatedAt();
        var currentCreatedAtValue = updatedUser.getCreatedAt();
        assertThat(previousCreatedAtValue).isCloseTo(currentCreatedAtValue, within(1, ChronoUnit.MILLIS));

        var previousUpdatedAtValue = testUser.getUpdatedAt();
        var currentUpdatedAtValue = updatedUser.getUpdatedAt();
        assertThat(currentUpdatedAtValue).isAfter(previousUpdatedAtValue);
    }

    @Test
    @DisplayName("Should handle DELETE by ID correctly")
    void checkDeleteById() throws Exception {
        var request = delete("/api/users/" + testUser.getId()).with(testUserToken);
        mvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should handle invalid POST to create new User correctly")
    void checkCreateWithInvalidData() throws Exception {
        userRepository.deleteAll();
        var invalidInputData = UserCreateDto.builder()
                .password("12")
                .build();

        var badRequest = post("/api/users")
                .with(testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidInputData));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Email is required')]").exists())
                .andExpect(jsonPath("$.details[?(@ == 'Password must be at least 3 characters long')]").exists());

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should handle POST to create new User with duplicate email correctly")
    void checkCreateWithDuplicateEmail() throws Exception {
        var duplicateEmail = testUser.getEmail();
        var inputUserData = UserCreateDto.builder()
                .email(duplicateEmail)
                .password("123")
                .build();

        var badRequest = post("/api/users")
                .with(testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUserData));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Uniqueness violation"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Email " + duplicateEmail + " already exists')]").exists());
    }

    @Test
    @DisplayName("Should handle GET by ID when User not found correctly")
    void checkShowByIdNotFound() throws Exception {
        var invalidId = Long.MAX_VALUE;
        var request = get("/api/users/" + invalidId).with(testUserToken);
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'User with id=" + invalidId + " not found')]").exists());
    }

    @Test
    @DisplayName("Should handle invalid PUT to update correctly")
    void checkUpdateWithInvalidEmailAndPassword() throws Exception {
        var invalidDataToUpdate = UserCreateDto.builder()
                .email("invalid")
                .password("12")
                .build();

        var badRequest = put("/api/users/" + testUser.getId())
                .with(testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDataToUpdate));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Invalid email format')]").exists())
                .andExpect(jsonPath("$.details[?(@ == 'Password must be at least 3 characters long')]").exists());

        var actualUser = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(actualUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", testUser.getEmail())
                .hasFieldOrPropertyWithValue("password", testUser.getPassword());
    }

    @Test
    @DisplayName("Should handle PUT to update when User not found correctly")
    void checkUpdateWhenNotFound() throws Exception {
        var updatedUserData = Instancio.of(modelGenerator.getUserUpdatedData()).create();
        var invalidId = Long.MAX_VALUE;

        var request = put("/api/users/" + invalidId)
                .with(testUserToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserData));

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'User with id=" + invalidId + " not found')]").exists());
    }

}
