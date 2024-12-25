package hexlet.code.mapper;

/**
 * Abstract Entity mapper.
 *
 * @param <T>  - Entity type
 * @param <CD> - Create Entity Dto type
 * @param <UD> - Update Entity Dto type
 * @param <RD> - Response Entity Dto type
 */
public abstract class AbstractMapper<T, CD, UD, RD> {

    protected abstract T toDomain(CD dto);

    protected abstract RD domainTo(T entity);

    protected abstract T update(T entity, UD dto);

}
