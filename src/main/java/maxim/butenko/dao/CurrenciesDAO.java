package maxim.butenko.dao;

import java.util.List;
import java.util.Optional;

public interface CurrenciesDAO<K, T> {

    List<T> findAll();

    Optional<T> findById(K id);

    boolean deleteById(K id);

    void update(T t);

    T save(T t);
}