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

    public abstract T toDomain(CD dto);

    public abstract RD domainTo(T entity);

    public abstract T update(T entity, UD dto);

}
