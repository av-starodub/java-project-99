package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import hexlet.code.dto.task.TaskCreateDto;
import hexlet.code.dto.task.TaskUpdateDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.hasItem;
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
public final class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    private User testUser;

    private TaskStatus testStatus;

    private Task testTask;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        var user = Instancio.of(modelGenerator.getUserModel()).create();
        testUser = userRepository.save(user);

        var status = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testStatus = statusRepository.save(status);

        var task = Instancio.of(modelGenerator.getTaskModel()).create();
        task.setTaskStatus(testStatus);
        task.setAssignee(testUser);
        testTask = taskRepository.save(task);
    }

    @AfterAll
    void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
    }

    @Test
    @DisplayName("Should handle GET to show all existing tasks correctly")
    void checkGetAllTasks() throws Exception {
        var expectedTasksSize = taskRepository.count();
        var request = get("/api/tasks").with(token);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedTasksSize))
                .andExpect(header().string("X-Total-Count", String.valueOf(expectedTasksSize)))
                .andExpect(jsonPath("$[*].id").value(hasItem(testTask.getId().intValue())))
                .andExpect(jsonPath("$[*].assignee_id").value(hasItem(testUser.getId().intValue())))
                .andExpect(jsonPath("$[*].status").value(hasItem(testStatus.getSlug())));
    }

    @Test
    @DisplayName("Should handle GET to show all tasks with filters correctly")
    void checkGetWithFilter() throws Exception {
        var requestWithNameAsFilter = get("/api/tasks?"
                + "titleCont=" + testTask.getName())
                .with(token);
        var resultBodyByNameFilter = mvc.perform(requestWithNameAsFilter)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].id").value(hasItem(testTask.getId().intValue())))
                .andExpect(jsonPath("$[*].assignee_id").value(hasItem(testUser.getId().intValue())))
                .andExpect(jsonPath("$[*].status").value(hasItem(testStatus.getSlug())))
                .andReturn().getResponse().getContentAsString();

        var requestWithSlugAndAssigneeAsFilter = get("/api/tasks?"
                + "status=" + testTask.getStatusSlug()
                + "&assigneeId=" + testTask.getAssigneeId())
                .with(token);
        var resultBodyByStatusAndAssignee = mvc.perform(requestWithSlugAndAssigneeAsFilter)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        assertThat(resultBodyByNameFilter).isEqualTo(resultBodyByStatusAndAssignee);

    }

    @Test
    @DisplayName("Should handle valid POST to create new Task correctly")
    void checkCreateTask() throws Exception {
        var taskCreateDto = TaskCreateDto.builder()
                .index(testTask.getIndex() + 1)
                .title("New task")
                .content("New task")
                .assigneeId(testUser.getId())
                .status(testStatus.getSlug())
                .taskLabelIds(testTask.getLabelIds())
                .build();

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCreateDto));

        var body = mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(taskCreateDto.getTitle()))
                .andExpect(jsonPath("$.content").value(taskCreateDto.getContent()))
                .andExpect(jsonPath("$.assignee_id").value(taskCreateDto.getAssigneeId()))
                .andExpect(jsonPath("$.status").value(taskCreateDto.getStatus()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var savedTaskId = JsonPath.<Integer>read(body, "$.id").longValue();
        var savedTask = taskRepository.findWithRelationsById(savedTaskId).orElse(null);

        assertThat(savedTask)
                .isNotNull()
                .hasFieldOrPropertyWithValue("index", taskCreateDto.getIndex())
                .hasFieldOrPropertyWithValue("name", taskCreateDto.getTitle())
                .hasFieldOrPropertyWithValue("description", taskCreateDto.getContent())
                .hasFieldOrPropertyWithValue("taskStatus", testStatus)
                .hasFieldOrPropertyWithValue("assignee", testUser)
                .hasFieldOrProperty("createdAt").isNotNull();
    }

    @Test
    @DisplayName("Should handle GET by ID correctly")
    void checkShowById() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var body = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value(testTask.getName()))
                .andExpect(jsonPath("$.content").value(testTask.getDescription()))
                .andExpect(jsonPath("$.assignee_id").value(testTask.getAssigneeId()))
                .andExpect(jsonPath("$.status").value(testTask.getStatusSlug()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdAt = JsonPath.<String>read(body, "$.createdAt");
        assertThat(testTask.getCreatedAt()).isCloseTo(createdAt, within(1, ChronoUnit.SECONDS));

    }

    @Test
    @DisplayName("Should handle GET by ID when Task not found correctly")
    void checkShowByIdNotFound() throws Exception {
        var invalidId = Long.MAX_VALUE;
        var request = get("/api/tasks/" + invalidId).with(token);
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Task with id=" + invalidId + " not found')]").exists());
    }

    @Test
    @DisplayName("Should handle PUT to update only new Task data correctly")
    void checkUpdateTask() throws Exception {
        var updateDto = TaskUpdateDto.builder()
                .title("New title")
                .content("New content")
                .build();

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        var expectedTitle = updateDto.getTitle().orElse(null);
        var expectedContent = updateDto.getContent().orElse(null);

        var body = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.title").value(expectedTitle))
                .andExpect(jsonPath("$.content").value(expectedContent))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var actualCreatedAt = JsonPath.<String>read(body, "$.createdAt");
        assertThat(testTask.getCreatedAt()).isCloseTo(actualCreatedAt, within(1, ChronoUnit.SECONDS));

        var updatedTask = taskRepository.findWithRelationsById(testTask.getId()).orElse(null);
        assertThat(updatedTask)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", expectedTitle)
                .hasFieldOrPropertyWithValue("description", expectedContent);
    }

    @Test
    @DisplayName("Should handle DELETE by ID correctly")
    void checkDeleteById() throws Exception {
        var existTaskId = testTask.getId();
        var request = delete("/api/tasks/" + existTaskId).with(token);
        mvc.perform(request).andExpect(status().isNoContent());
        assertThat(taskRepository.findById(existTaskId)).isEmpty();
    }

    @Test
    @DisplayName("Should handle invalid POST to create a new Task correctly")
    void checkCreateWithInvalidData() throws Exception {
        taskRepository.deleteAll();
        var invalidData = TaskCreateDto.builder()
                .title("")
                .status("")
                .taskLabelIds(Collections.singletonList(null))
                .build();

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'Title is required to create a new task')]").exists())
                .andExpect(jsonPath("$.details[?(@ == 'Status slug is required to create a new task')]").exists())
                .andExpect(jsonPath("$.details[?(@ == '" + TaskCreateDto.LABEL_NULL_ERROR_MESSAGE + "')]").exists());

        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should handle invalid PUT to update task correctly")
    void checkUpdateWithInvalidData() throws Exception {
        var updateDto = TaskUpdateDto.builder()
                .title(" ")
                .labelIds(Collections.singletonList(null))
                .build();

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));

        var expectedTitle = testTask.getName();
        var expectedLabels = testTask.getLabels();

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Input data validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@ == 'The name should not consist only of spaces')]").exists())
                .andExpect(jsonPath("$.details[?(@ == '" + TaskCreateDto.LABEL_NULL_ERROR_MESSAGE + "')]").exists());

        var actualTask = taskRepository.findWithRelationsById(testTask.getId()).orElse(null);
        assertThat(actualTask)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", expectedTitle);
        var actualTaskLabels = actualTask.getLabels();
        assertThat(actualTaskLabels)
                .doesNotContainNull()
                .hasSize(expectedLabels.size());
    }

}
