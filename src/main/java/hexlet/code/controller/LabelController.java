package hexlet.code.controller;

import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelResponseDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.service.LabelService;
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
public final class LabelController {

    private final LabelService labelService;

    private final LabelMapper labelMapper;

    @PostMapping("/labels")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDto create(@Valid @RequestBody LabelCreateDto createDto) {
        var newLabel = labelService.create(createDto);
        return labelMapper.domainTo(newLabel);
    }

    @GetMapping("/labels")
    public ResponseEntity<List<LabelResponseDto>> index() {
        var labels = labelService.getAll();
        System.out.println("LABELS: " + labels);
        var labelDtos = labels.stream()
                .map(labelMapper::domainTo)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labelDtos.size()))
                .body(labelDtos);
    }

    @GetMapping("/labels/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDto show(@PathVariable Long id) {
        return labelService.getById(id)
                .map(labelMapper::domainTo)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id=%s not found".formatted(id)));
    }

    @PutMapping("/labels/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponseDto update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDto updateDto) {
        return labelService.update(id, updateDto)
                .map(labelMapper::domainTo)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id=%s not found".formatted(id)));
    }

    @DeleteMapping("/labels/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        labelService.delete(id);
    }

}
