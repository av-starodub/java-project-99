package hexlet.code.controller;

import hexlet.code.dto.status.StatusCreateDto;
import hexlet.code.dto.status.StatusResponseDto;
import hexlet.code.dto.status.StatusUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
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

    private final TaskStatusService service;

    private final TaskStatusMapper mapper;

    @PostMapping("/task_statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public StatusResponseDto create(@Valid @RequestBody StatusCreateDto createDto) {
        var newStatus = service.create(createDto);
        return mapper.domainTo(newStatus);
    }

    @GetMapping("/task_statuses")
    public ResponseEntity<List<StatusResponseDto>> index() {
        var statuses = service.getAll();
        var statusDtos = statuses.stream()
                .map(mapper::domainTo)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statusDtos.size()))
                .body(statusDtos);
    }

    @GetMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StatusResponseDto show(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::domainTo)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id=%d not found".formatted(id)));
    }

    @DeleteMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StatusResponseDto update(@PathVariable Long id, @Valid @RequestBody StatusUpdateDto updateDto) {
        return service.update(id, updateDto)
                .map(mapper::domainTo)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id=%d not found".formatted(id)));
    }

}
