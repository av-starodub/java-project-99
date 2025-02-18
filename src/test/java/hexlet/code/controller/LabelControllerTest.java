package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import hexlet.code.component.DataInitializer;
import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.model.DefaultLabelType;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public final class LabelControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    private Label testLabel;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testLabel = new Label("test");
    }

    @AfterEach
    void tearDown() {
        labelRepository.deleteAll();
    }

    @Test
    @DisplayName("Should handle GET to show all existing Labels correctly")
    void checkGetAllLabels() throws Exception {
        wac.getBean(DataInitializer.class).run(null);

        var expectedExistingLabelNames = DefaultLabelType.getAllDefaultLabelNames();

        var request = get("/api/labels").with(token);

        var body = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedExistingLabelNames.size()))
                .andExpect(header().string("X-Total-Count", String.valueOf(expectedExistingLabelNames.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualLabelNames = JsonPath.read(body, "$[*].name");
        assertThat(actualLabelNames).isEqualTo(expectedExistingLabelNames);
    }

    @Test
    @DisplayName("Should handle valid POST to create Label correctly")
    void checkCreateLabel() throws Exception {
        var newLabelName = testLabel.getName();
        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LabelCreateDto(newLabelName)));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(newLabelName))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        var savedLabel = labelRepository.findByName(newLabelName).orElse(null);
        assertThat(savedLabel)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", newLabelName);
        assertThat(savedLabel.getCreatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should handle GET Label by ID correctly")
    void checkShowById() throws Exception {
        var savedLabel = labelRepository.save(testLabel);
        assertThat(savedLabel).isNotNull();

        var savedLabelId = savedLabel.getId();
        var expectedCreatedAt = savedLabel.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        var request = get("/api/labels/" + savedLabelId).with(token);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedLabelId))
                .andExpect(jsonPath("$.name").value(savedLabel.getName()))
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt));
    }

    @Test
    @DisplayName("Should handle DELETE Label by ID correctly")
    void checkDeleteById() throws Exception {
        var savedLabel = labelRepository.save(testLabel);

        var savedLabelId = savedLabel.getId();
        var request = delete("/api/labels/" + savedLabelId).with(token);
        mvc.perform(request).andExpect(status().isNoContent());

        assertThat(labelRepository.findById(savedLabelId)).isEmpty();
    }

    @Test
    @DisplayName("Should handle PUT to update correctly")
    void checkUpdateLabel() throws Exception {
        var savedLabel = labelRepository.save(testLabel);
        assertThat(savedLabel).isNotNull();

        var savedLabelId = savedLabel.getId();
        var labelUpdateDto = new LabelUpdateDto("updated");
        var expectedCreatedAt = savedLabel.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        var request = put("/api/labels/" + savedLabelId)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labelUpdateDto));
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedLabelId))
                .andExpect(jsonPath("$.name").value(labelUpdateDto.getName()))
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt));

        var updatedLabel = labelRepository.findByName(labelUpdateDto.getName()).orElse(null);
        assertThat(updatedLabel)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", savedLabelId)
                .hasFieldOrPropertyWithValue("name", labelUpdateDto.getName())
                .hasFieldOrPropertyWithValue("createdAt", savedLabel.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle GET by ID when Label not found correctly")
    void checkShowByIdNotFound() throws Exception {
        var invalidId = Long.MAX_VALUE;
        var request = get("/api/labels/" + invalidId).with(token);
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Label with id=" + invalidId + " not found')]").exists());
    }

    @Test
    @DisplayName("Should handle invalid POST to create Label correctly")
    void checkCreateWithInvalidData() throws Exception {
        var invalidData = new LabelCreateDto("");
        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData));
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Name is required')]").exists())
                .andExpect(jsonPath("$.details[?(@ == '" + LabelCreateDto.NAME_SIZE_ERROR_MESSAGE + "')]").exists());

        assertThat(labelRepository.findByName(invalidData.getName())).isEmpty();
    }

    @Test
    @DisplayName("Should handle invalid PUT to update Label correctly")
    void checkUpdateWithInvalidData() throws Exception {
        var savedLabel = labelRepository.save(testLabel);
        assertThat(savedLabel).isNotNull();

        var savedLabelId = savedLabel.getId();
        var invalidData = new LabelUpdateDto("");
        var badRequest = put("/api/labels/" + savedLabelId)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData));
        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Name is required')]").exists())
                .andExpect(jsonPath("$.details[?(@ == '" + LabelCreateDto.NAME_SIZE_ERROR_MESSAGE + "')]").exists());

        var expectedName = savedLabel.getName();
        var actualLabel =  labelRepository.findById(savedLabelId).orElse(null);
        assertThat(actualLabel)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", expectedName);
    }

    @Test
    @DisplayName("Should handle invalid POST to create Label with duplicate name correctly")
    void checkCreateWithDuplicateName() throws Exception {
        var savedLabel = labelRepository.save(testLabel);
        assertThat(savedLabel).isNotNull();

        var duplicateName = savedLabel.getName();
        var createDtoWithDuplicateName = new LabelCreateDto(duplicateName);
        var badRequest = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDtoWithDuplicateName));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Uniqueness violation"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Label " + duplicateName + " already exists')]").exists());

        var sameLabels = labelRepository.findAll().stream()
                .filter(label -> label.getName().equals(savedLabel.getName()))
                .toList();
        assertThat(sameLabels)
                .hasSize(1)
                .containsOnly(savedLabel);
    }

    @Test
    @DisplayName("Should handle DELETE by ID Label associated with Task correctly")
    void checkDeleteByIdLabelUsed() throws Exception {

    }

}
