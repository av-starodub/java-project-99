package hexlet.code.mapper;

/**
 * Abstract Entity mapper.
 *
 * @param <T>  - Domain type
 * @param <CD> - Create Dto type
 * @param <UD> - Update Dto type
 * @param <RD> - Response Dto type
 */
public abstract class AbstractMapper<T, CD, UD, RD> {

    protected abstract T toDomain(CD dto);

    protected abstract RD domainTo(T domain);

    protected abstract T update(T domain, UD dto);

}
