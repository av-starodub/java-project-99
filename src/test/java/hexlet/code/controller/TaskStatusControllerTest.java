package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import hexlet.code.component.DataInitializer;
import hexlet.code.dto.status.StatusCreateDto;
import hexlet.code.dto.status.StatusUpdateDto;
import hexlet.code.model.DefaultTaskStatusType;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import hexlet.code.util.ModelGenerator;
import org.assertj.core.api.InstanceOfAssertFactories;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.BDDAssertions.as;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class TaskStatusControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    private TaskStatus testStatus;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
    }

    @AfterAll
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should handle GET to show all existing TaskStatus correctly")
    void checkGetAllTaskStatus() throws Exception {
        wac.getBean(DataInitializer.class).run(null);

        var expectedExistingStatusNames = DefaultTaskStatusType.getAllDefaultStatusNames();

        var request = get("/api/task_statuses").with(token);

        var body = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedExistingStatusNames.size()))
                .andExpect(header().string("X-Total-Count", String.valueOf(expectedExistingStatusNames.size())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> actualStatusNames = JsonPath.read(body, "$[*].name");
        assertThat(actualStatusNames).isEqualTo(expectedExistingStatusNames);
    }

    @Test
    @DisplayName("Should handle valid POST to create TaskStatus correctly")
    void checkCreateTaskStatus() throws Exception {
        var statusCreateDto = StatusCreateDto.builder()
                .name(testStatus.getName())
                .slug(testStatus.getSlug())
                .build();

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusCreateDto));

        var expectedSavedName = testStatus.getName();
        var expectedSavedSlug = testStatus.getSlug();
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(expectedSavedName))
                .andExpect(jsonPath("$.slug").value(expectedSavedSlug))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        var savedStatus = taskStatusService.getBySlug(testStatus.getSlug()).orElse(null);
        assertThat(savedStatus)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", expectedSavedName)
                .hasFieldOrPropertyWithValue("slug", expectedSavedSlug);
        assertThat(savedStatus.getCreatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should handle GET by ID correctly")
    void checkShowById() throws Exception {
        var savedStatus = repository.save(testStatus);
        assertThat(savedStatus).isNotNull();

        var savedStatusId = savedStatus.getId();
        var expectedCreatedAt = savedStatus.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        var request = get("/api/task_statuses/" + savedStatusId).with(token);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedStatusId))
                .andExpect(jsonPath("$.name").value(savedStatus.getName()))
                .andExpect(jsonPath("$.slug").value(savedStatus.getSlug()))
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt));
    }

    @Test
    @DisplayName("Should handle DELETE by ID correctly")
    void checkDeleteById() throws Exception {
        var savedStatus = repository.save(testStatus);
        assertThat(savedStatus).isNotNull();

        var savedStatusId = savedStatus.getId();
        var request = delete("/api/task_statuses/" + savedStatusId).with(token);

        mvc.perform(request).andExpect(status().isNoContent());

        assertThat(repository.findById(savedStatusId)).isEmpty();
    }

    @Test
    @DisplayName("Should handle PUT to update only new data correctly")
    void checkUpdateTaskStatus() throws Exception {
        var savedStatus = repository.save(testStatus);
        assertThat(savedStatus).isNotNull();

        var savedStatusId = savedStatus.getId();
        var newSlug = "update";
        var updateDto = StatusUpdateDto.builder()
                .slug(newSlug)
                .build();
        var expectedCreatedAt = savedStatus.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        var request = put("/api/task_statuses/" + savedStatusId)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedStatusId))
                .andExpect(jsonPath("$.name").value(testStatus.getName()))
                .andExpect(jsonPath("$.slug").value(newSlug))
                .andExpect(jsonPath("$.createdAt").value(expectedCreatedAt))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var updatedStatus = repository.findBySlug(newSlug).orElse(null);
        assertThat(updatedStatus)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", savedStatusId)
                .hasFieldOrPropertyWithValue("name", testStatus.getName())
                .hasFieldOrPropertyWithValue("slug", newSlug)
                .extracting(TaskStatus::getCreatedAt, as(InstanceOfAssertFactories.LOCAL_DATE_TIME))
                .isCloseTo(savedStatus.getCreatedAt(), within(1, ChronoUnit.MILLIS));
    }

    @Test
    @DisplayName("Should handle GET by ID when TaskStatus not found correctly")
    void checkShowByIdNotFound() throws Exception {
        var invalidId = Long.MAX_VALUE;
        var request = get("/api/task_statuses/" + invalidId).with(token);
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'TaskStatus with id=" + invalidId + " not found')]").exists());
    }

    @Test
    @DisplayName("Should handle invalid POST to create TaskStatus correctly")
    void checkCreateWithInvalidData() throws Exception {
        var invalidData = StatusCreateDto.builder()
                .slug("")
                .build();

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Name is required')]").exists())
                .andExpect(jsonPath("$.details[?(@ == '" + StatusCreateDto.SLUG_SIZE_ERROR_MESSAGE + "')]").exists());

        assertThat(repository.findBySlug(invalidData.getSlug())).isEmpty();
    }

    @Test
    @DisplayName("Should handle invalid PUT to update TaskStatus correctly")
    void checkUpdateWithInvalidData() throws Exception {
        var savedStatus = repository.save(testStatus);
        assertThat(savedStatus).isNotNull();

        var savedStatusId = savedStatus.getId();
        var invalidData = StatusUpdateDto.builder()
                .slug("")
                .build();

        var request = put("/api/task_statuses/" + savedStatusId)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == '" + StatusCreateDto.SLUG_SIZE_ERROR_MESSAGE + "')]").exists());

        var expectedSlug = savedStatus.getSlug();
        var actualStatus = taskStatusService.getBySlug(expectedSlug).orElse(null);
        assertThat(actualStatus)
                .isNotNull()
                .hasFieldOrPropertyWithValue("slug", expectedSlug);
    }

    @Test
    @DisplayName("Should handle invalid POST to create TaskStatus with duplicate name and slug correctly")
    void checkCreateWithDuplicateNameAndSlug() throws Exception {
        var savedStatus = repository.save(testStatus);
        assertThat(savedStatus).isNotNull();

        var duplicateName = testStatus.getName();
        var duplicateSlug = testStatus.getSlug();
        var duplicateStatusCreateDto = StatusCreateDto.builder()
                .name(duplicateName)
                .slug(duplicateSlug)
                .build();

        var badRequest = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateStatusCreateDto));

        mvc.perform(badRequest)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Uniqueness violation"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Duplicate value breaks unique constraint')]").exists());

        var expectedSlug = savedStatus.getSlug();
        var expectedName = savedStatus.getName();
        var sameStatuses = repository.findAll().stream()
                .filter(s -> s.getSlug().equals(expectedSlug) || s.getName().equals(expectedName))
                .toList();
        assertThat(sameStatuses)
                .hasSize(1)
                .containsOnly(savedStatus);
    }

    @Test
    @DisplayName("Should handle invalid DELETE when TaskStatus used")
    void checkDeleteWhenStatusInUse() throws Exception {
        var savedStatus = repository.save(testStatus);
        var task = Instancio.of(modelGenerator.getTaskModel()).create();
        task.setTaskStatus(savedStatus);
        var taskRepository = wac.getBean(TaskRepository.class);
        taskRepository.save(task);

        var savedStatusId = savedStatus.getId();
        var request = delete("/api/task_statuses/" + savedStatusId).with(token);

        mvc.perform(request)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Removing the resource used"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath(
                        "$.details[?(@ == 'Entity is referenced by other objects')]")
                        .exists());

        var actualStatus = taskStatusService.getBySlug(savedStatus.getSlug()).orElse(null);
        assertThat(actualStatus).isEqualTo(savedStatus);

        taskRepository.deleteAll();
    }

}
