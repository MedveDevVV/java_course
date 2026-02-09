package autoservice.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<K, E, F> {
    E save(E entity);
    List<E> findAll();
    List<E> findAll(F filter);
    Optional<E> findById(K id);
    boolean update(E entity);
    boolean delete(E entity);
}

