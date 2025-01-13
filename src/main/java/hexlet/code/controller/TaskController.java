package hexlet.code.controller;

import hexlet.code.dto.task.TaskCreateDto;
import hexlet.code.dto.task.TaskResponseDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public final class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDto>> index() {
        var tasks = taskService.getAll();
        var taskDtos = tasks.stream()
                .map(taskMapper::domainTo)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskDtos.size()))
                .body(taskDtos);
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto create(@Valid @RequestBody TaskCreateDto createDto) {
        var newTask = taskService.create(createDto);
        return taskMapper.domainTo(newTask);
    }
}
