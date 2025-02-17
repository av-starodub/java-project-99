package hexlet.code.mapper;

import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelResponseDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.model.Label;
import org.springframework.stereotype.Service;

@Service
public final class LabelMapper extends AbstractMapper<Label, LabelCreateDto, LabelUpdateDto, LabelResponseDto> {

    @Override
    public Label toDomain(LabelCreateDto dto) {
        return null;
    }

    @Override
    public LabelResponseDto domainTo(Label entity) {
        return null;
    }

    @Override
    public Label update(Label entity, LabelUpdateDto dto) {
        return null;
    }

}
