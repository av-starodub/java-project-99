package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Service
public final class LabelService {

    private final LabelRepository repository;

    private final LabelMapper mapper;

    public Label create(LabelCreateDto createDto) {
        var newLabel = mapper.toDomain(createDto);
        return repository.save(newLabel);
    }

    public Optional<Label> getById(Long id) {
        return repository.findById(id);
    }

    public List<Label> getAll() {
        return repository.findAll();
    }

    public Set<Label> getAllByIds(List<Long> ids) {
        return nonNull(ids) ? Set.copyOf(repository.findAllById(ids)) : Collections.emptySet();
    }

    public Optional<Label> update(Long id, LabelUpdateDto updateDto) {
        return repository.findById(id)
                .map(label -> mapper.update(label, updateDto))
                .map(repository::save);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

}
