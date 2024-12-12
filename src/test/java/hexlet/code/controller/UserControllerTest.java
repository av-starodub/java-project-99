package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
    }

    @Test
    @DisplayName("Should create new User correctly")
    public void checkCreateUser() throws Exception {
        var inputUserData = Instancio.of(modelGenerator.getUserCreateDto()).create();

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

        var newUser = userRepository.findByEmail(inputUserData.getEmail()).orElse(null);
        assertThat(newUser).isNotNull();
        assertThat(encoder.matches(inputUserData.getPassword(), newUser.getPassword())).isTrue();
        assertThat(newUser)
                .hasFieldOrPropertyWithValue("firstName", inputUserData.getFirstName())
                .hasFieldOrPropertyWithValue("lastName", inputUserData.getLastName())
                .hasFieldOrProperty("createdAt").isNotNull()
                .hasFieldOrProperty("updatedAt").isNotNull();
    }

}
