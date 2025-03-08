package hexlet.code.mapper;

import hexlet.code.dto.base.CreateDto;
import hexlet.code.dto.base.ResponseDto;
import hexlet.code.dto.base.UpdateDto;
import hexlet.code.model.BaseEntity;


/**
 * Abstract Entity mapper.
 *
 * @param <T>  - Entity type
 * @param <CD> - Create Entity Dto type
 * @param <UD> - Update Entity Dto type
 * @param <RD> - Response Entity Dto type
 */
public abstract class AbstractMapper
        <T extends BaseEntity, CD extends CreateDto, UD extends UpdateDto, RD extends ResponseDto> {

    public abstract T toDomain(CD dto);

    public abstract RD domainTo(T entity);

    public abstract T update(T entity, UD dto);

}
