package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should create new User correctly on POST /users with valid user input")
    void checkCreate() throws Exception {
        var inputUserData = Instancio.of(modelGenerator.getUserData()).create();

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUserData));
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(inputUserData.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(inputUserData.getLastName()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

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
    @DisplayName("Should handle missing required fields in the input data correctly")
    void checkCreateMissingRequiredFields() throws Exception {
        var invalidInputData = Instancio.of(modelGenerator.getUserDataWithoutRequiredFields()).create();

        var badRequest = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidInputData));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Email is required')]").exists())
                .andExpect(jsonPath("$.details[?(@ == 'Password is required')]").exists());
    }

    @Test
    @DisplayName("Should handle invalid email format and password length in the user data correctly")
    void checkCreateWithInvalidEmailAndPassword() throws Exception {
        var invalidInputData = Instancio.of(
                modelGenerator.getUserDataWithInvalidEmailAndPassword()
        ).create();

        var badRequest = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidInputData));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Invalid email format')]").exists())
                .andExpect(jsonPath("$.details[?(@ == 'Password must be at least 3 characters long')]").exists());
    }

    @Test
    @DisplayName("Should handle POST /users with duplicate email correctly")
    void checkCreateDuplicateEmail() throws Exception {
        var inputUserData = Instancio.of(modelGenerator.getUserData()).create();

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUserData));
        mvc.perform(request).andExpect(status().isCreated());

        var badRequest = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUserData));
        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Constraint violation"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Email must be unique')]").exists());
    }

    @Test
    @DisplayName("Should handle no request body correctly")
    void checkCreateNoRequestBody() throws Exception {
        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Email is required')]").exists())
                .andExpect(jsonPath("$.details[?(@ == 'Password is required')]").exists());
    }

    @Test
    @DisplayName("Should return all saved users on GET /users")
    void checkGetAllUsers() throws Exception {
        var expectedUsers = userService.getAll();
        var testUserIdx = expectedUsers.indexOf(testUser);
        Function<String, String> toPath = (key) -> "$[%d].%s".formatted(testUserIdx, key);

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(userRepository.count()))
                .andExpect(jsonPath(toPath.apply("id")).isNotEmpty())
                .andExpect(jsonPath(toPath.apply("firstName")).value(testUser.getFirstName()))
                .andExpect(jsonPath(toPath.apply("lastName")).value(testUser.getLastName()))
                .andExpect(jsonPath(toPath.apply("email")).value(testUser.getEmail()))
                .andExpect(jsonPath(toPath.apply("createdAt")).isNotEmpty())
                .andExpect(jsonPath(toPath.apply("updatedAt")).isNotEmpty())
                .andExpect(jsonPath(toPath.apply("password")).doesNotExist());
    }

    @Test
    @DisplayName("Should return user by 'id' on GET /users/{id}")
    void checkShowById() throws Exception {
        mvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("Should handle GET /users/{id} when user not found correctly")
    void checkShowByIdNotFound() throws Exception {
        var invalidId = Long.MAX_VALUE;
        mvc.perform(get("/api/users/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'User with id=" + invalidId + " not found')]").exists());
    }

    @Test
    @DisplayName("Should handle DELETE /users/{id} correctly")
    void checkDeleteById() throws Exception {
        mvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isNoContent());
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should update only new data")
    void checkUpdateOnlyNewData() throws Exception {
        var updatedUserData = Instancio.of(modelGenerator.getUserUpdatedData()).create();

        var request = put("/api/users/" + testUser.getId())
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
                .hasFieldOrPropertyWithValue("email", newEmail)
                .hasFieldOrPropertyWithValue("createdAt", testUser.getCreatedAt());


        var newPassword = updatedUserData.getPassword().orElse(null);
        assertThat(encoder.matches(newPassword, updatedUser.getPasswordHash())).isTrue();

        assertThat(updatedUser.getCreatedAt()).isEqualTo(testUser.getCreatedAt());

        var previousUpdatedAtValue = testUser.getUpdatedAt();
        var currentUpdatedAtValue = updatedUser.getUpdatedAt();
        assertThat(currentUpdatedAtValue).isAfter(previousUpdatedAtValue);
    }

    @Test
    @DisplayName("Should handle user update with invalid email format and password length correctly")
    void checkUpdateWithInvalidEmailAndPassword() throws Exception {
        var invalidDataForUpdate = Instancio.of(
                modelGenerator.getUserDataWithInvalidEmailAndPassword()
        ).create();

        var badRequest = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDataForUpdate));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Invalid email format')]").exists())
                .andExpect(jsonPath("$.details[?(@ == 'Password must be at least 3 characters long')]").exists());
    }

    @Test
    @DisplayName("Should handle update when user not found correctly")
    void checkUpdateWhenNotFound() throws Exception {
        var updatedUserData = Instancio.of(modelGenerator.getUserUpdatedData()).create();
        var invalidId = Long.MAX_VALUE;

        var request = put("/api/users/" + invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserData));

        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'User with id=" + invalidId + " not found')]").exists());
    }

}
