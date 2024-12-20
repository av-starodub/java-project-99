package hexlet.code.controller;

import hexlet.code.dto.status.StatusCreateDto;
import hexlet.code.dto.status.StatusResponseDto;
import hexlet.code.dto.status.StatusUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public StatusResponseDto create(@Valid @RequestBody StatusCreateDto createDto) {
        var newStatus = taskStatusService.create(createDto);
        return statusToDto(newStatus);
    }

    @GetMapping("/task_statuses")
    public ResponseEntity<List<StatusResponseDto>> index() {
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
    public StatusResponseDto show(@PathVariable Long id) {
        return taskStatusService.getById(id)
                .map(this::statusToDto)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id=%d not found".formatted(id)));
    }

    private StatusResponseDto statusToDto(TaskStatus taskStatus) {
        return StatusResponseDto.builder()
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
    public StatusResponseDto update(@PathVariable Long id, @Valid @RequestBody StatusUpdateDto updateDto) {
        return taskStatusService.update(id, updateDto)
                .map(this::statusToDto)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id=%d not found".formatted(id)));
    }

}
