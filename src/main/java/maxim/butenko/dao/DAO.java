package maxim.butenko.dao;

import java.util.List;

public interface DAO<T> {

    List<T> findAll();

    T update(T t);

    T save(T t);
}