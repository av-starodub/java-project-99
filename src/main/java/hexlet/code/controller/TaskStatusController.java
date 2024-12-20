package hexlet.code.controller;

import hexlet.code.dto.ErrorDto;
import hexlet.code.dto.status.TaskStatusCreateDto;
import hexlet.code.dto.status.TaskStatusDto;
import hexlet.code.dto.status.TaskStatusUpdateDto;
import hexlet.code.exception.DuplicateTaskStatusException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public final class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @PostMapping("/task_statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDto create(@Valid @RequestBody TaskStatusCreateDto createDto) {
        var newStatus = taskStatusService.create(createDto);
        return statusToDto(newStatus);
    }

    @GetMapping("/task_statuses")
    public ResponseEntity<List<TaskStatusDto>> index() {
        var statuses = taskStatusService.getAll();
        var statusDtos = statuses.stream()
                .map(this::statusToDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statusDtos.size()))
                .body(statusDtos);
    }

    @GetMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDto show(@PathVariable Long id) {
        return taskStatusService.getById(id)
                .map(this::statusToDto)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id=%d not found".formatted(id)));
    }

    private TaskStatusDto statusToDto(TaskStatus taskStatus) {
        return TaskStatusDto.builder()
                .id(taskStatus.getId())
                .name(taskStatus.getName())
                .slug(taskStatus.getSlug())
                .createdAt(taskStatus.getCreatedAt())
                .build();
    }

    @DeleteMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }

    @PutMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDto update(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDto updateDto) {
        return taskStatusService.update(id, updateDto)
                .map(this::statusToDto)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id=%d not found".formatted(id)));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ErrorDto("Resource not found", List.of(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationException(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return new ErrorDto("Validation failed", details);
    }

    @ExceptionHandler(DuplicateTaskStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDataIntegrityViolation(DuplicateTaskStatusException ex) {
        return new ErrorDto("Constraint violation", ex.getDetails());
    }

}
